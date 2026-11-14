package com.navnirman.payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long>{
 Optional<PaymentOrder> findByRazorpayOrderId(String orderId);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from PaymentOrder p where p.razorpayOrderId = :orderId") Optional<PaymentOrder> lockByRazorpayOrderId(@Param("orderId") String orderId);
 Optional<PaymentOrder> findByRazorpayPaymentId(String paymentId);
}
