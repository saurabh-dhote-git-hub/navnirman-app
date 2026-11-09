package com.navnirman.content;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="content_categories", uniqueConstraints=@UniqueConstraint(name="uk_category_slug",columnNames="slug"))
public class ContentCategory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=110,unique=true) private String slug;
 @Column(length=500) private String description;
 @Column(nullable=false) private boolean active=true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected ContentCategory(){}
 public ContentCategory(String name,String slug,String description){this.name=name;this.slug=slug;this.description=description;}
 public Long getId(){return id;} public String getName(){return name;} public String getSlug(){return slug;}
 public String getDescription(){return description;} public boolean isActive(){return active;}
 public void update(String n,String s,String d,boolean a){name=n;slug=s;description=d;active=a;}
}
