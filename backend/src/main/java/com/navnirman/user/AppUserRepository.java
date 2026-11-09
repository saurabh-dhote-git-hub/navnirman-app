package com.navnirman.user;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppUserRepository extends JpaRepository<AppUser,Long>{
 boolean existsByEmailIgnoreCase(String email); boolean existsByMobileNumber(String mobile);
 Optional<AppUser> findByEmailIgnoreCase(String email); Optional<AppUser> findByMobileNumber(String mobile);
}
