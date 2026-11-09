package com.navnirman.content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<ContentCategory,Long>{
 boolean existsBySlugIgnoreCase(String slug);
 List<ContentCategory> findAllByOrderByNameAsc();
 List<ContentCategory> findByActiveTrueOrderByNameAsc();
}
