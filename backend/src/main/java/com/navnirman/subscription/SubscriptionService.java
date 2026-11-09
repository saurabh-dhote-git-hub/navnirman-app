package com.navnirman.subscription;
import com.navnirman.user.*;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;import org.springframework.web.server.ResponseStatusException;
import java.time.*;import java.util.*;
import static com.navnirman.subscription.SubscriptionDtos.*;
@Service public class SubscriptionService {
 private final PlanRepository plans;private final SubscriptionRepository subscriptions;private final AppUserRepository users;
 public SubscriptionService(PlanRepository p,SubscriptionRepository s,AppUserRepository u){plans=p;subscriptions=s;users=u;}
 @Transactional public void seedPlans(){
  if(plans.findByCodeAndActiveTrue("MONTHLY").isEmpty()){
   plans.save(new SubscriptionPlan("MONTHLY","Monthly","30 days of Navnirman Premium access",5000,30));
  }
  plans.findAll().stream().filter(p->!p.getCode().equals("MONTHLY")).forEach(p->plans.deleteById(p.getId()));
 }
 @Transactional(readOnly=true) public List<PlanResponse> plans(){return plans.findByActiveTrueOrderByPricePaiseAsc().stream().map(p->new PlanResponse(p.getId(),p.getCode(),p.getName(),p.getDescription(),p.getPricePaise(),p.getDurationDays())).toList();}
 @Transactional(readOnly=true) public List<SubscriptionResponse> mine(String email){return subscriptions.findByUser_EmailIgnoreCaseOrderByCreatedAtDesc(email).stream().map(this::dto).toList();}
 @Transactional(readOnly=true) public boolean hasPremium(String email){return subscriptions.existsByUser_EmailIgnoreCaseAndStatusAndStartsAtLessThanEqualAndEndsAtGreaterThan(email,SubscriptionStatus.ACTIVE,Instant.now(),Instant.now());}
 @Transactional public SubscriptionResponse grant(String email,String code,int days,String source){
  AppUser user=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found"));
  SubscriptionPlan plan=plans.findByCodeAndActiveTrue(code).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown or inactive plan"));
  int duration=days>0?Math.min(days,3660):plan.getDurationDays();Instant now=Instant.now();
  return dto(subscriptions.save(new Subscription(user,plan,now,now.plus(Duration.ofDays(duration)),source)));
 }
 @Transactional public SubscriptionResponse cancel(Long id,String email,boolean admin){
  Subscription s=subscriptions.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Subscription not found"));
  if(!admin&&!s.getUser().getEmail().equalsIgnoreCase(email))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not your subscription");
  s.cancel();return dto(s);
 }
 private SubscriptionResponse dto(Subscription s){return new SubscriptionResponse(s.getId(),s.getStatus().name(),s.getPlan().getCode(),s.getPlan().getName(),s.getStartsAt(),s.getEndsAt(),s.isActiveAt(Instant.now()));}
}
