package com.navnirman.subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.*;
public interface SubscriptionRepository extends JpaRepository<Subscription,Long>{
 Optional<Subscription> findFirstByUser_EmailIgnoreCaseAndStatusOrderByEndsAtDesc(String email,SubscriptionStatus status);
 List<Subscription> findByUser_EmailIgnoreCaseOrderByCreatedAtDesc(String email);
 boolean existsByUser_EmailIgnoreCaseAndStatusAndStartsAtLessThanEqualAndEndsAtGreaterThan(String email,SubscriptionStatus status,Instant now,Instant end);
}
