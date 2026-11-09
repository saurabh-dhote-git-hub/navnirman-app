package com.navnirman.auth;
import jakarta.validation.constraints.*;
public final class AuthDtos {private AuthDtos(){}
 public record RegisterRequest(@NotBlank @Size(min=2,max=120) String fullName,@NotBlank @Email @Size(max=254) String email,
  @NotBlank @Pattern(regexp="^[0-9]{10,15}$") String mobileNumber,@NotBlank @Size(min=8,max=72) String password){}
 public record LoginRequest(@NotBlank String identifier,@NotBlank String password){}
 public record UserResponse(Long id,String fullName,String email,String mobileNumber,String role){}
 public record AuthResponse(String accessToken,String tokenType,long expiresInSeconds,UserResponse user){}
}
