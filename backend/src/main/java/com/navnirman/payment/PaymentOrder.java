package com.navnirman.payment;
import com.navnirman.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="payment_orders", uniqueConstraints={
 @UniqueConstraint(name="uk_payment_razorpay_order",columnNames="razorpay_order_id"),
 @UniqueConstraint(name="uk_payment_razorpay_payment",columnNames="razorpay_payment_id")})
public class PaymentOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @Column(nullable=false,length=40) private String planCode;
 @Column(nullable=false) private long amountPaise;
 @Column(nullable=false,length=3) private String currency;
 @Column(nullable=false,length=80,unique=true) private String receipt;
 @Column(name="razorpay_order_id",nullable=false,length=80,unique=true) private String razorpayOrderId;
 @Column(name="razorpay_payment_id",length=80,unique=true) private String razorpayPaymentId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private PaymentStatus status=PaymentStatus.CREATED;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @Column private Instant paidAt;
 protected PaymentOrder(){}
 public PaymentOrder(AppUser user,String planCode,long amount,String currency,String receipt,String orderId){this.user=user;this.planCode=planCode;amountPaise=amount;this.currency=currency;this.receipt=receipt;razorpayOrderId=orderId;}
 public Long getId(){return id;} public AppUser getUser(){return user;} public String getPlanCode(){return planCode;}
 public long getAmountPaise(){return amountPaise;} public String getCurrency(){return currency;} public String getReceipt(){return receipt;}
 public String getRazorpayOrderId(){return razorpayOrderId;} public String getRazorpayPaymentId(){return razorpayPaymentId;}
 public PaymentStatus getStatus(){return status;} public Instant getCreatedAt(){return createdAt;} public Instant getPaidAt(){return paidAt;}
 public void markPaid(String paymentId){if(status==PaymentStatus.PAID)return;razorpayPaymentId=paymentId;status=PaymentStatus.PAID;paidAt=Instant.now();}
 public void markFailed(){if(status!=PaymentStatus.PAID)status=PaymentStatus.FAILED;}
}
