package com.navnirman.auth;
import com.navnirman.auth.AuthDtos.*;import com.navnirman.security.JwtService;import com.navnirman.user.*;
import org.springframework.beans.factory.annotation.Value;import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service public class AuthService {
 private final AppUserRepository users;private final PasswordEncoder encoder;private final JwtService jwt;private final long minutes;
 public AuthService(AppUserRepository u,PasswordEncoder e,JwtService j,@Value("${app.jwt.expiration-minutes:60}")long m){users=u;encoder=e;jwt=j;minutes=m;}
 @Transactional public AuthResponse register(RegisterRequest r){
  String email=r.email().trim().toLowerCase(),mobile=r.mobileNumber().trim();
  if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
  if(users.existsByMobileNumber(mobile))throw new ResponseStatusException(HttpStatus.CONFLICT,"Mobile number already registered");
  return response(users.save(new AppUser(r.fullName().trim(),email,mobile,encoder.encode(r.password()),Role.CUSTOMER)));
 }
 public AuthResponse customerLogin(LoginRequest r){AppUser u=find(r.identifier());if(u.getRole()!=Role.CUSTOMER||!encoder.matches(r.password(),u.getPasswordHash()))throw invalid();return response(u);}
 public AuthResponse adminLogin(LoginRequest r){AppUser u=find(r.identifier());if(u.getRole()!=Role.ADMIN||!encoder.matches(r.password(),u.getPasswordHash()))throw invalid();return response(u);}
 public UserResponse currentUser(String email){return user(users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found")));}
 private AppUser find(String id){String v=id.trim();return (v.contains("@")?users.findByEmailIgnoreCase(v):users.findByMobileNumber(v)).orElseThrow(AuthService::invalid);}
 private AuthResponse response(AppUser u){return new AuthResponse(jwt.generateToken(u),"Bearer",minutes*60,user(u));}
 private UserResponse user(AppUser u){return new UserResponse(u.getId(),u.getFullName(),u.getEmail(),u.getMobileNumber(),u.getRole().name());}
 private static ResponseStatusException invalid(){return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");}
}
