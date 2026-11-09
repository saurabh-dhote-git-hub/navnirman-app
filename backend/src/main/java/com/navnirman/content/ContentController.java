package com.navnirman.content;
import com.navnirman.content.ContentDtos.*;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.core.io.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import com.navnirman.subscription.SubscriptionService;
@RestController
public class ContentController {
 private final ContentService service; private final Path storage; private final SubscriptionService subscriptions;
 public ContentController(ContentService s,SubscriptionService subscriptions,@Value("${app.storage.video-dir:./storage/videos}")String dir)throws IOException{service=s;this.subscriptions=subscriptions;storage=Paths.get(dir).toAbsolutePath().normalize();Files.createDirectories(storage);}
 @GetMapping("/api/categories") public List<CategoryResponse> categories(){return service.categories(false);}
 @GetMapping("/api/videos") public List<VideoResponse> publicVideos(){return service.videos(false);}
 @GetMapping("/api/admin/dashboard") public DashboardResponse dashboard(){return service.dashboard();}
 @GetMapping("/api/admin/categories") public List<CategoryResponse> adminCategories(){return service.categories(true);}
 @PostMapping("/api/admin/categories") public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest r){return ResponseEntity.status(201).body(service.createCategory(r));}
 @PutMapping("/api/admin/categories/{id}") public CategoryResponse updateCategory(@PathVariable Long id,@RequestBody CategoryRequest r){return service.updateCategory(id,r);}
 @DeleteMapping("/api/admin/categories/{id}") public ResponseEntity<Void> deleteCategory(@PathVariable Long id){service.deleteCategory(id);return ResponseEntity.noContent().build();}
 @GetMapping("/api/admin/videos") public List<VideoResponse> adminVideos(){return service.videos(true);}
 @PostMapping(value="/api/admin/videos",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<VideoResponse> upload(@RequestParam String titleMr,@RequestParam String titleHi,@RequestParam String titleEn,
  @RequestParam(required=false)String descriptionMr,@RequestParam(required=false)String descriptionHi,@RequestParam(required=false)String descriptionEn,
  @RequestParam Long categoryId,@RequestParam(defaultValue="false")boolean premium,@RequestParam(defaultValue="false")boolean published,
  @RequestParam(required=false)String thumbnailUrl,@RequestPart("file")MultipartFile file)throws IOException{
  if(file.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a video file");
  String type=file.getContentType()==null?"":file.getContentType();
  if(!type.startsWith("video/"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only video files are allowed");
  String ext=Optional.ofNullable(file.getOriginalFilename()).filter(n->n.contains(".")).map(n->n.substring(n.lastIndexOf('.'))).orElse(".mp4");
  String stored=UUID.randomUUID()+ext.toLowerCase(Locale.ROOT);Path target=storage.resolve(stored).normalize();
  if(!target.startsWith(storage))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid filename");
  try(var in=file.getInputStream()){Files.copy(in,target);}
  try{
   var v=new VideoItem(required(titleMr),required(titleHi),required(titleEn),descriptionMr,descriptionHi,descriptionEn,
    category(categoryId),file.getOriginalFilename()==null?"video":file.getOriginalFilename(),stored,type,file.getSize(),thumbnailUrl,premium,published);
   return ResponseEntity.status(201).body(service.createVideo(v));
  }catch(RuntimeException ex){Files.deleteIfExists(target);throw ex;}
 }
 @PutMapping("/api/admin/videos/{id}") public VideoResponse updateVideo(@PathVariable Long id,@RequestBody VideoUpdateRequest r){
  return service.updateVideo(id,new ContentService.VideoUpdate(required(r.titleMr()),required(r.titleHi()),required(r.titleEn()),r.descriptionMr(),r.descriptionHi(),r.descriptionEn(),r.categoryId(),r.thumbnailUrl(),r.premium(),r.published()));
 }
 @DeleteMapping("/api/admin/videos/{id}") public ResponseEntity<Void> deleteVideo(@PathVariable Long id)throws IOException{var v=service.entity(id);Path p=storage.resolve(v.getStoredFilename()).normalize();service.deleteVideo(id);Files.deleteIfExists(p);return ResponseEntity.noContent().build();}
 @GetMapping("/api/videos/{id}/stream")
 public ResponseEntity<byte[]> stream(@PathVariable Long id,@RequestHeader(value="Range",required=false)String range,Authentication authentication)throws IOException{
  var entity=service.entity(id);
  if(!entity.isPublished())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Video is not published");
  if(entity.isPremium()){
   if(authentication==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sign in to watch Premium content");
   if(!subscriptions.hasPremium(authentication.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"An active Premium subscription is required");
  }
  Path file=storage.resolve(entity.getStoredFilename()).normalize();
  if(!file.startsWith(storage)||!Files.exists(file))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Video file not found");
  long length=Files.size(file);if(length==0)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Video file is empty");
  long start=0,end=length-1;
  if(range!=null&&!range.isBlank()){
   if(!range.startsWith("bytes=")||range.contains(","))throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,"Only one byte range is supported");
   try{
    String[] parts=range.substring(6).split("-",2);
    if(parts.length!=2)throw new IllegalArgumentException();
    if(parts[0].isBlank()){long suffix=Long.parseLong(parts[1]);if(suffix<=0)throw new IllegalArgumentException();start=Math.max(0,length-suffix);}
    else {start=Long.parseLong(parts[0]);if(!parts[1].isBlank())end=Long.parseLong(parts[1]);}
    if(start<0||start>=length||end<start)throw new IllegalArgumentException();end=Math.min(end,length-1);
   }catch(RuntimeException ex){throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,"Invalid byte range");}
  }
  int size=(int)Math.min(end-start+1,1024*1024);
  if(end-start+1>1024*1024)end=start+size-1;
  byte[] bytes=new byte[size];
  try(var raf=new java.io.RandomAccessFile(file.toFile(),"r")){raf.seek(start);raf.readFully(bytes);}
  HttpHeaders headers=new HttpHeaders();headers.setContentType(MediaType.parseMediaType(entity.getContentType()));
  headers.set(HttpHeaders.ACCEPT_RANGES,"bytes");headers.setContentLength(bytes.length);
  headers.set(HttpHeaders.CACHE_CONTROL,"private, no-store");
  if(range!=null){headers.set(HttpHeaders.CONTENT_RANGE,"bytes "+start+"-"+end+"/"+length);return new ResponseEntity<>(bytes,headers,HttpStatus.PARTIAL_CONTENT);}
  if(length>bytes.length){headers.set(HttpHeaders.CONTENT_RANGE,"bytes "+start+"-"+end+"/"+length);return new ResponseEntity<>(bytes,headers,HttpStatus.PARTIAL_CONTENT);}
  return new ResponseEntity<>(bytes,headers,HttpStatus.OK);
 }
 private ContentCategory category(Long id){return service.category(id);}
 private String required(String s){if(s==null||s.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Marathi, Hindi and English titles are required");return s.trim();}
 public record VideoUpdateRequest(String titleMr,String titleHi,String titleEn,String descriptionMr,String descriptionHi,String descriptionEn,Long categoryId,String thumbnailUrl,boolean premium,boolean published){}
}
