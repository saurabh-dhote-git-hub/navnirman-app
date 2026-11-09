package com.navnirman.security;
import com.navnirman.user.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service public class JwtService {
 private final SecretKey key; private final long minutes;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-minutes:60}") long minutes){
  if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalStateException("JWT_SECRET must be at least 32 bytes");
  key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.minutes=minutes;
 }
 public String generateToken(AppUser u){Instant now=Instant.now();return Jwts.builder().subject(u.getEmail()).claim("role",u.getRole().name()).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(minutes*60))).signWith(key).compact();}
 public String extractSubject(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
 public boolean isValid(String token){try{Jwts.parser().verifyWith(key).build().parseSignedClaims(token);return true;}catch(RuntimeException ex){return false;}}
}
