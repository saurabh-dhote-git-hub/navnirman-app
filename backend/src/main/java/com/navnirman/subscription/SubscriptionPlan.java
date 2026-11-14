package com.navnirman.subscription;
import jakarta.persistence.*;
@Entity @Table(name="subscription_plans")
public class SubscriptionPlan {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=40) private String code;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=500) private String description;
 @Column(nullable=false) private long pricePaise;
 @Column(nullable=false) private int durationDays;
 @Column(nullable=false) private boolean active=true;
 protected SubscriptionPlan(){}
 public SubscriptionPlan(String code,String name,String description,long pricePaise,int durationDays){this.code=code;this.name=name;this.description=description;this.pricePaise=pricePaise;this.durationDays=durationDays;}
 public void configureMonthly(){this.name="Monthly";this.description="30 days of Navnirman Premium access";this.pricePaise=5000;this.durationDays=30;this.active=true;}
 public void deactivate(){this.active=false;}
 public Long getId(){return id;} public String getCode(){return code;} public String getName(){return name;} public String getDescription(){return description;}
 public long getPricePaise(){return pricePaise;} public int getDurationDays(){return durationDays;} public boolean isActive(){return active;}
}
