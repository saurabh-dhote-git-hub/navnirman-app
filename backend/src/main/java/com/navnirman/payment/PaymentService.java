package com.navnirman.payment;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.navnirman.subscription.*;
import com.navnirman.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Base64;
@Service
public class PaymentService {
 private final PaymentOrderRepository orders; private final AppUserRepository users; private final PlanRepository plans;
 private final SubscriptionService subscriptions; private final ObjectMapper mapper;
 private final String keyId,keySecret,webhookSecret;
 private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
 public PaymentService(PaymentOrderRepository orders,AppUserRepository users,PlanRepository plans,SubscriptionService subscriptions,ObjectMapper mapper,
  @Value("${app.razorpay.key-id:}")String keyId,@Value("${app.razorpay.key-secret:}")String keySecret,@Value("${app.razorpay.webhook-secret:}")String webhookSecret){
  this.orders=orders;this.users=users;this.plans=plans;this.subscriptions=subscriptions;this.mapper=mapper;this.keyId=keyId;this.keySecret=keySecret;this.webhookSecret=webhookSecret;
 }
 public PaymentDtos.CreateOrderResponse createOrder(String email){
  requireConfigured();
  AppUser user=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found"));
  SubscriptionPlan plan=plans.findByCodeAndActiveTrue("MONTHLY").orElseThrow(()->new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Monthly plan is unavailable"));
  if(plan.getPricePaise()!=5000||plan.getDurationDays()!=30)throw new ResponseStatusException(HttpStatus.CONFLICT,"Monthly plan must be configured as ₹50 for 30 days");
  String receipt="nav_"+user.getId()+"_"+System.currentTimeMillis()+"_"+ThreadLocalRandom.current().nextInt(1000,9999);
  try{
   ObjectNode body=mapper.createObjectNode();body.put("amount",5000);body.put("currency","INR");body.put("receipt",receipt);
   ObjectNode notes=body.putObject("notes");notes.put("customer_email",user.getEmail());notes.put("plan_code","MONTHLY");
   HttpRequest request=HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1/orders")).timeout(Duration.ofSeconds(25))
    .header("Authorization",basicAuth()).header("Content-Type","application/json")
    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
   HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()<200||response.statusCode()>=300)throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Razorpay order creation failed. Check API keys and account configuration.");
   JsonNode data=mapper.readTree(response.body());String orderId=data.path("id").asText();
   if(orderId.isBlank()||data.path("amount").asLong()!=5000||!"INR".equals(data.path("currency").asText()))throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Razorpay returned an unexpected order.");
   orders.save(new PaymentOrder(user,"MONTHLY",5000,"INR",receipt,orderId));
   return new PaymentDtos.CreateOrderResponse(keyId,orderId,5000,"INR",plan.getName(),user.getFullName(),user.getEmail(),user.getMobileNumber());
  }catch(ResponseStatusException e){throw e;}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Could not create Razorpay order.");}
 }
 @Transactional
 public PaymentDtos.PaymentResponse verifyCheckout(String email,PaymentDtos.VerifyRequest request){
  requireConfigured();
  PaymentOrder order=orders.lockByRazorpayOrderId(request.razorpayOrderId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment order not found"));
  if(!order.getUser().getEmail().equalsIgnoreCase(email))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This payment order does not belong to you");
  if(order.getStatus()==PaymentStatus.PAID)return dto(order);
  if(request.razorpayPaymentId()==null||request.razorpaySignature()==null||!validPaymentSignature(order.getRazorpayOrderId(),request.razorpayPaymentId(),request.razorpaySignature()))
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Payment signature verification failed");
  JsonNode payment=fetchPayment(request.razorpayPaymentId());
  if(!order.getRazorpayOrderId().equals(payment.path("order_id").asText())||payment.path("amount").asLong()!=order.getAmountPaise()||
    !order.getCurrency().equals(payment.path("currency").asText())||!"captured".equalsIgnoreCase(payment.path("status").asText()))
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Payment is not captured or does not match this order");
  activate(order,request.razorpayPaymentId());
  return dto(order);
 }
 @Transactional
 public void processWebhook(byte[] raw,String signature){
  if(webhookSecret.isBlank())throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Webhook secret is not configured");
  if(signature==null||!constantTimeEquals(hmacHex(webhookSecret,raw),signature))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid webhook signature");
  try{
   JsonNode root=mapper.readTree(raw);String event=root.path("event").asText();
   if("payment.captured".equals(event)){
    JsonNode payment=root.path("payload").path("payment").path("entity");
    String orderId=payment.path("order_id").asText(),paymentId=payment.path("id").asText();
    PaymentOrder order=orders.lockByRazorpayOrderId(orderId).orElse(null);
    if(order!=null&&payment.path("amount").asLong()==order.getAmountPaise()&&order.getCurrency().equals(payment.path("currency").asText())&&"captured".equalsIgnoreCase(payment.path("status").asText()))
      activate(order,paymentId);
   }else if("payment.failed".equals(event)){
    JsonNode payment=root.path("payload").path("payment").path("entity");orders.findByRazorpayOrderId(payment.path("order_id").asText()).ifPresent(PaymentOrder::markFailed);
   }
  }catch(ResponseStatusException e){throw e;}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid webhook payload");}
 }
 private void activate(PaymentOrder order,String paymentId){
  if(order.getStatus()==PaymentStatus.PAID)return;
  if(orders.findByRazorpayPaymentId(paymentId).filter(p->!p.getId().equals(order.getId())).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Payment already applied to another order");
  order.markPaid(paymentId);
  subscriptions.grant(order.getUser().getEmail(),"MONTHLY",30,"RAZORPAY");
 }
 private JsonNode fetchPayment(String paymentId){
  try{
   HttpRequest request=HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1/payments/"+paymentId)).timeout(Duration.ofSeconds(25)).header("Authorization",basicAuth()).GET().build();
   HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()<200||response.statusCode()>=300)throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Unable to confirm payment status with Razorpay");
   return mapper.readTree(response.body());
  }catch(ResponseStatusException e){throw e;}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Unable to confirm payment status with Razorpay");}
 }
 private boolean validPaymentSignature(String orderId,String paymentId,String signature){return constantTimeEquals(hmacHex(keySecret,(orderId+"|"+paymentId).getBytes(StandardCharsets.UTF_8)),signature);}
 private String basicAuth(){return "Basic "+Base64.getEncoder().encodeToString((keyId+":"+keySecret).getBytes(StandardCharsets.UTF_8));}
 private String hmacHex(String secret,byte[] body){try{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));return HexFormat.of().formatHex(mac.doFinal(body));}catch(Exception e){throw new IllegalStateException(e);}}
 private boolean constantTimeEquals(String a,String b){return a!=null&&b!=null&&MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8));}
 private void requireConfigured(){if(keyId.isBlank()||keySecret.isBlank())throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Razorpay is not configured. Add Razorpay test keys to backend environment variables.");}
 private PaymentDtos.PaymentResponse dto(PaymentOrder o){return new PaymentDtos.PaymentResponse(o.getStatus().name(),o.getRazorpayOrderId(),o.getRazorpayPaymentId(),o.getAmountPaise(),o.getCurrency(),o.getPaidAt());}
}
