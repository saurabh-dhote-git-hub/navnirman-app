package com.navnirman.payment;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/payments")
public class PaymentController {
 private final PaymentService service;
 public PaymentController(PaymentService service){this.service=service;}
 @PostMapping("/orders") public ResponseEntity<PaymentDtos.CreateOrderResponse> createOrder(Authentication auth){
  return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(auth.getName()));
 }
 @PostMapping("/verify") public PaymentDtos.PaymentResponse verify(Authentication auth,@RequestBody PaymentDtos.VerifyRequest request){
  return service.verifyCheckout(auth.getName(),request);
 }
 @PostMapping(value="/webhook",consumes=MediaType.APPLICATION_JSON_VALUE)
 public ResponseEntity<Void> webhook(@RequestBody byte[] raw,@RequestHeader(value="X-Razorpay-Signature",required=false)String signature){
  service.processWebhook(raw,signature);return ResponseEntity.ok().build();
 }
}
