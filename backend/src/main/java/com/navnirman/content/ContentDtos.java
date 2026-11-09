package com.navnirman.content;
import java.time.Instant;
public final class ContentDtos {private ContentDtos(){}
 public record CategoryRequest(String name,String slug,String description,Boolean active){}
 public record CategoryResponse(Long id,String name,String slug,String description,boolean active){}
 public record VideoResponse(Long id,String titleMr,String titleHi,String titleEn,String descriptionMr,String descriptionHi,String descriptionEn,
  Long categoryId,String categoryName,String originalFilename,String contentType,long fileSize,String thumbnailUrl,boolean premium,boolean published,Instant createdAt,Instant updatedAt,String streamUrl){}
 public record DashboardResponse(long totalVideos,long publishedVideos,long draftVideos,long totalCustomers,long totalCategories){}
}
