package com.navnirman.subscription;
import java.time.Instant;
public final class SubscriptionDtos {private SubscriptionDtos(){}
 public record PlanResponse(Long id,String code,String name,String description,long pricePaise,int durationDays){}
 public record SubscriptionResponse(Long id,String status,String planCode,String planName,Instant startsAt,Instant endsAt,boolean active){}
 public record AdminGrantRequest(String customerEmail,String planCode,int durationDays){}
}
