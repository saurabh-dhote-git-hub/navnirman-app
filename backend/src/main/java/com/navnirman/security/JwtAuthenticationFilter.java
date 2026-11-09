package com.navnirman.security;
import com.navnirman.user.AppUserRepository;
import jakarta.servlet.*;import jakarta.servlet.http.*;
import java.io.IOException;import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwt;private final AppUserRepository users;
 public JwtAuthenticationFilter(JwtService j,AppUserRepository u){jwt=j;users=u;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")){String token=h.substring(7);
   if(jwt.isValid(token)){var user=users.findByEmailIgnoreCase(jwt.extractSubject(token)).orElse(null);
    if(user!=null&&user.isEnabled()){var auth=new UsernamePasswordAuthenticationToken(user.getEmail(),null,List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));SecurityContextHolder.getContext().setAuthentication(auth);}
   }
  }chain.doFilter(req,res);
 }
}
