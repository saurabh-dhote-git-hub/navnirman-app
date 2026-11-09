package com.navnirman.auth;
import com.navnirman.auth.AuthDtos.*;import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") public class AuthController {
 private final AuthService service;public AuthController(AuthService s){service=s;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AuthResponse register(@Valid @RequestBody RegisterRequest r){return service.register(r);}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){return service.customerLogin(r);}
 @PostMapping("/admin/login") public AuthResponse adminLogin(@Valid @RequestBody LoginRequest r){return service.adminLogin(r);}
 @GetMapping("/me") public UserResponse me(Authentication a){return service.currentUser(a.getName());}
}
