package com.navnirman.subscription;
import org.springframework.boot.ApplicationArguments;import org.springframework.boot.ApplicationRunner;import org.springframework.stereotype.Component;
@Component public class SubscriptionBootstrap implements ApplicationRunner {
 private final SubscriptionService service;public SubscriptionBootstrap(SubscriptionService s){service=s;}
 public void run(ApplicationArguments args){service.seedPlans();}
}
