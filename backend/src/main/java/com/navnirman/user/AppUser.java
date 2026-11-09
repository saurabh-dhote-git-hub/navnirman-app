package com.navnirman.user;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="app_users", uniqueConstraints={
 @UniqueConstraint(name="uk_app_user_email",columnNames="email"),
 @UniqueConstraint(name="uk_app_user_mobile",columnNames="mobile_number")})
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="full_name",nullable=false,length=120) private String fullName;
 @Column(nullable=false,length=254) private String email;
 @Column(name="mobile_number",nullable=false,length=16) private String mobileNumber;
 @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role=Role.CUSTOMER;
 @Column(nullable=false) private boolean enabled=true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected AppUser() {}
 public AppUser(String n,String e,String m,String p,Role r){fullName=n;email=e;mobileNumber=m;passwordHash=p;role=r;}
 public Long getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;}
 public String getMobileNumber(){return mobileNumber;} public String getPasswordHash(){return passwordHash;}
 public Role getRole(){return role;} public boolean isEnabled(){return enabled;}
}
