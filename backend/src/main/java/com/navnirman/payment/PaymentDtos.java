package com.navnirman.payment;
import java.time.Instant;
public final class PaymentDtos {private PaymentDtos(){}
 public record CreateOrderResponse(String keyId,String orderId,long amount,String currency,String planName,String customerName,String customerEmail,String customerMobile){}
 public record VerifyRequest(String razorpayOrderId,String razorpayPaymentId,String razorpaySignature){}
 public record PaymentResponse(String status,String razorpayOrderId,String razorpayPaymentId,long amountPaise,String currency,Instant paidAt){}
}
