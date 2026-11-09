package com.navnirman.content;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="videos")
public class VideoItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=180) private String titleMr;
 @Column(nullable=false,length=180) private String titleHi;
 @Column(nullable=false,length=180) private String titleEn;
 @Column(length=3000) private String descriptionMr;
 @Column(length=3000) private String descriptionHi;
 @Column(length=3000) private String descriptionEn;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id",nullable=false) private ContentCategory category;
 @Column(nullable=false,length=255) private String originalFilename;
 @Column(nullable=false,length=255,unique=true) private String storedFilename;
 @Column(nullable=false,length=120) private String contentType;
 @Column(nullable=false) private long fileSize;
 @Column(length=1000) private String thumbnailUrl;
 @Column(nullable=false) private boolean premium;
 @Column(nullable=false) private boolean published;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @Column(nullable=false) private Instant updatedAt=Instant.now();
 protected VideoItem(){}
 public VideoItem(String mr,String hi,String en,String dmr,String dhi,String den,ContentCategory cat,String original,String stored,String type,long size,String thumb,boolean premium,boolean published){
  titleMr=mr;titleHi=hi;titleEn=en;descriptionMr=dmr;descriptionHi=dhi;descriptionEn=den;category=cat;originalFilename=original;storedFilename=stored;contentType=type;fileSize=size;thumbnailUrl=thumb;this.premium=premium;this.published=published;
 }
 public void update(String mr,String hi,String en,String dmr,String dhi,String den,ContentCategory cat,String thumb,boolean prem,boolean pub){titleMr=mr;titleHi=hi;titleEn=en;descriptionMr=dmr;descriptionHi=dhi;descriptionEn=den;category=cat;thumbnailUrl=thumb;premium=prem;published=pub;updatedAt=Instant.now();}
 public Long getId(){return id;} public String getTitleMr(){return titleMr;} public String getTitleHi(){return titleHi;} public String getTitleEn(){return titleEn;}
 public String getDescriptionMr(){return descriptionMr;} public String getDescriptionHi(){return descriptionHi;} public String getDescriptionEn(){return descriptionEn;}
 public ContentCategory getCategory(){return category;} public String getOriginalFilename(){return originalFilename;} public String getStoredFilename(){return storedFilename;}
 public String getContentType(){return contentType;} public long getFileSize(){return fileSize;} public String getThumbnailUrl(){return thumbnailUrl;}
 public boolean isPremium(){return premium;} public boolean isPublished(){return published;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
