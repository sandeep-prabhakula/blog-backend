package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.repository.ReactiveBlogRepository;
import com.sandeepprabhakula.blogging.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class BlogController {

    private final BlogService blogService;
    private final ReactiveBlogRepository blogRepository;
    private ServerHttpRequest request;
    private final Logger log = LoggerFactory.getLogger(BlogController.class);

    @GetMapping("/get-all-blogs")
    public Flux<Blog> getAllBlogs(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5",required = false)int pageSize
            ){
        getIPAddress("get-all-blogs");
        return blogService.getAllBlogs(pageNumber,pageSize);
    }

    @GetMapping("/blog/{id}")
    public Mono<Blog> getBlogById(@PathVariable("id")String id){
        getIPAddress("blog");
        return blogService.findBlogById(id);
    }

    @PostMapping("/add-blog")
    public ResponseEntity<Mono<String>> addNewBlog(@RequestBody Blog blog){
        getIPAddress("add-blog");
        Mono<String> response = blogService.addNewBlog(blog);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog")
    public ResponseEntity<Mono<String>> updateBlog(@RequestBody Blog blog){
        getIPAddress("update-blog");
        Mono<String> response = blogService.updateBlog(blog);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}")
    public ResponseEntity<Mono<String>> deleteBlog(@PathVariable("blogId")String blogId){
        getIPAddress("delete-blog");
        Mono<String> res = blogService.deleteBlog(blogId); // will throw NOT_FOUND if not found
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search-blogs/{prompt}")
    public Flux<Blog> search(@PathVariable("prompt")String prompt){
        getIPAddress("search-blogs");
        return blogService.search(prompt);
    }

    private void getIPAddress(String endpoint){
        try {
            log.info("{} accessed from ip addr: {}", endpoint,request.getRemoteAddress());
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", e.getStatusCode().value());
        body.put("message", e.getReason());
        return new ResponseEntity<>(body, e.getStatusCode());
    }

}
