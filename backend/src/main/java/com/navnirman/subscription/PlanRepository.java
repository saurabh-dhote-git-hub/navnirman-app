package com.navnirman.subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PlanRepository extends JpaRepository<SubscriptionPlan,Long>{Optional<SubscriptionPlan> findByCodeAndActiveTrue(String code);List<SubscriptionPlan> findByActiveTrueOrderByPricePaiseAsc();}
