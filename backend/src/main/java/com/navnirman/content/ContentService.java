package com.navnirman.content;
import com.navnirman.content.ContentDtos.*;
import com.navnirman.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@Service public class ContentService {
 private final CategoryRepository categories; private final VideoRepository videos; private final AppUserRepository users;
 public ContentService(CategoryRepository c,VideoRepository v,AppUserRepository u){categories=c;videos=v;users=u;}
 @Transactional(readOnly=true) public List<CategoryResponse> categories(boolean admin){return (admin?categories.findAllByOrderByNameAsc():categories.findByActiveTrueOrderByNameAsc()).stream().map(this::categoryDto).toList();}
 @Transactional public CategoryResponse createCategory(CategoryRequest r){validateCategory(r);String slug=slug(r.slug());if(categories.existsBySlugIgnoreCase(slug))throw new ResponseStatusException(HttpStatus.CONFLICT,"Category slug already exists");return categoryDto(categories.save(new ContentCategory(r.name().trim(),slug,r.description())));}
 @Transactional public CategoryResponse updateCategory(Long id,CategoryRequest r){validateCategory(r);var c=category(id);String slug=slug(r.slug());if(!c.getSlug().equalsIgnoreCase(slug)&&categories.existsBySlugIgnoreCase(slug))throw new ResponseStatusException(HttpStatus.CONFLICT,"Category slug already exists");c.update(r.name().trim(),slug,r.description(),r.active()==null||r.active());return categoryDto(c);}
 @Transactional public void deleteCategory(Long id){var c=category(id);if(videos.findAll().stream().anyMatch(v->v.getCategory().getId().equals(id)))throw new ResponseStatusException(HttpStatus.CONFLICT,"Category is in use by videos. Deactivate it instead.");categories.delete(c);}
 @Transactional(readOnly=true) public List<VideoResponse> videos(boolean admin){return (admin?videos.findAllByOrderByCreatedAtDesc():videos.findByPublishedTrueOrderByCreatedAtDesc()).stream().map(this::videoDto).toList();}
 @Transactional(readOnly=true) public VideoResponse video(Long id){return videoDto(videos.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Video not found")));}
 @Transactional public VideoResponse createVideo(VideoItem v){return videoDto(videos.save(v));}
 @Transactional public VideoResponse updateVideo(Long id,VideoUpdate u){var v=videos.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Video not found"));v.update(u.titleMr(),u.titleHi(),u.titleEn(),u.descriptionMr(),u.descriptionHi(),u.descriptionEn(),category(u.categoryId()),u.thumbnailUrl(),u.premium(),u.published());return videoDto(v);}
 @Transactional public VideoItem entity(Long id){return videos.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Video not found"));}
 @Transactional public void deleteVideo(Long id){videos.delete(entity(id));}
 @Transactional(readOnly=true) public DashboardResponse dashboard(){long total=videos.count(),pub=videos.countByPublishedTrue();return new DashboardResponse(total,pub,total-pub,users.countByRole(Role.CUSTOMER),categories.count());}
 public ContentCategory category(Long id){return categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid category"));}
 private CategoryResponse categoryDto(ContentCategory c){return new CategoryResponse(c.getId(),c.getName(),c.getSlug(),c.getDescription(),c.isActive());}
 private VideoResponse videoDto(VideoItem v){return new VideoResponse(v.getId(),v.getTitleMr(),v.getTitleHi(),v.getTitleEn(),v.getDescriptionMr(),v.getDescriptionHi(),v.getDescriptionEn(),v.getCategory().getId(),v.getCategory().getName(),v.getOriginalFilename(),v.getContentType(),v.getFileSize(),v.getThumbnailUrl(),v.isPremium(),v.isPublished(),v.getCreatedAt(),v.getUpdatedAt(),"/api/videos/"+v.getId()+"/stream");}
 private void validateCategory(CategoryRequest r){if(r.name()==null||r.name().isBlank()||r.name().length()>100||r.slug()==null||r.slug().isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category name and slug are required");}
 private String slug(String s){return s.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","-").replaceAll("(^-|-$)","");}
 public record VideoUpdate(String titleMr,String titleHi,String titleEn,String descriptionMr,String descriptionHi,String descriptionEn,Long categoryId,String thumbnailUrl,boolean premium,boolean published){}
}
