package com.navnirman.subscription;
import com.navnirman.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="subscriptions",indexes=@Index(name="idx_subscription_user_status",columnList="user_id,status"))
public class Subscription {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="plan_id",nullable=false) private SubscriptionPlan plan;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private SubscriptionStatus status;
 @Column(nullable=false) private Instant startsAt;
 @Column(nullable=false) private Instant endsAt;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @Column(length=80) private String source;
 protected Subscription(){}
 public Subscription(AppUser user,SubscriptionPlan plan,Instant start,Instant end,String source){this.user=user;this.plan=plan;startsAt=start;endsAt=end;status=SubscriptionStatus.ACTIVE;this.source=source;}
 public Long getId(){return id;} public AppUser getUser(){return user;} public SubscriptionPlan getPlan(){return plan;} public SubscriptionStatus getStatus(){return status;}
 public Instant getStartsAt(){return startsAt;} public Instant getEndsAt(){return endsAt;} public String getSource(){return source;}
 public boolean isActiveAt(Instant now){return status==SubscriptionStatus.ACTIVE&&!now.isBefore(startsAt)&&now.isBefore(endsAt);}
 public void cancel(){status=SubscriptionStatus.CANCELLED;}
}
