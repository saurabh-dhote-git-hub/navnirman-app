package com.navnirman.health;
import java.time.Instant;import java.util.Map;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api") public class HealthController {
 @GetMapping("/health") public Map<String,Object> health(){return Map.of("application","navnirman-api","status","UP","version","0.2.0","timestamp",Instant.now().toString());}
}
