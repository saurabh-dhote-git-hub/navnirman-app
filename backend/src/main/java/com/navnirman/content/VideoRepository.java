package com.navnirman.content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VideoRepository extends JpaRepository<VideoItem,Long>{
 List<VideoItem> findByPublishedTrueOrderByCreatedAtDesc();
 List<VideoItem> findAllByOrderByCreatedAtDesc();
 long countByPublishedTrue();
}
