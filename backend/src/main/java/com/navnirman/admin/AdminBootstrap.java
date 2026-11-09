package com.navnirman.admin;
import com.navnirman.user.*;import org.springframework.boot.ApplicationArguments;import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.stereotype.Component;
@Component public class AdminBootstrap implements ApplicationRunner {
 private final Environment env;private final AppUserRepository users;private final PasswordEncoder encoder;
 public AdminBootstrap(Environment e,AppUserRepository u,PasswordEncoder p){env=e;users=u;encoder=p;}
 public void run(ApplicationArguments args){
  String name=env.getProperty("ADMIN_NAME"),email=env.getProperty("ADMIN_EMAIL"),password=env.getProperty("ADMIN_PASSWORD");
  if(blank(name)||blank(email)||blank(password))return;
  if(password.length()<12)throw new IllegalStateException("ADMIN_PASSWORD must be at least 12 characters");
  String normalized=email.trim().toLowerCase();if(users.findByEmailIgnoreCase(normalized).isPresent())return;
  users.save(new AppUser(name.trim(),normalized,"0000000000000000",encoder.encode(password),Role.ADMIN));
 }
 private boolean blank(String s){return s==null||s.isBlank();}
}
