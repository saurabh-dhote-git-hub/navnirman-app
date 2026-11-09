package com.navnirman.subscription;
import static com.navnirman.subscription.SubscriptionDtos.*;
import org.springframework.http.*;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController public class SubscriptionController {
 private final SubscriptionService service;public SubscriptionController(SubscriptionService s){service=s;}
 @GetMapping("/api/subscriptions/plans") public List<PlanResponse> plans(){return service.plans();}
 @GetMapping("/api/subscriptions/me") public List<SubscriptionResponse> mine(Authentication a){return service.mine(a.getName());}
 @PostMapping("/api/admin/subscriptions/grant") public ResponseEntity<SubscriptionResponse> grant(@RequestBody AdminGrantRequest r){return ResponseEntity.status(201).body(service.grant(r.customerEmail(),r.planCode(),r.durationDays(),"ADMIN_GRANT"));}
 @PostMapping("/api/subscriptions/{id}/cancel") public SubscriptionResponse cancel(@PathVariable Long id,Authentication a){return service.cancel(id,a.getName(),false);}
 @PostMapping("/api/admin/subscriptions/{id}/cancel") public SubscriptionResponse adminCancel(@PathVariable Long id,Authentication a){return service.cancel(id,a.getName(),true);}
}
