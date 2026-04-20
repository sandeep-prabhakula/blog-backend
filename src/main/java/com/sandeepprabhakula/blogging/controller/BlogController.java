package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.service.BlogService;
import com.sandeepprabhakula.blogging.util.AppUtilities;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class BlogController {
    private final Logger log = LoggerFactory.getLogger(BlogController.class);
    private final BlogService blogService;
    private final AppUtilities appUtilities;

    @GetMapping("/get-client-ip")
    public Map<String, Object> getClientIP(ServerHttpRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {

            String ip = appUtilities.getClientIPAddr(request);
            map.put("clientIP", ip);
            map.put("statusCode", 200);
            map.put("message","IP sent");
            return map;
        }catch (Exception e){
            map.put("clientIP", null);
            map.put("statusCode", 500);
            map.put("message",e.getMessage());
            return map;
        }
    }

    @GetMapping("/get-all-blogs")
    public Flux<Blog> getAllBlogs(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
            ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);

        log.info("Request from IP: {} to endpoint '/get-all-blogs?pageNumber={}&pageSize={}'.", firstIp, pageNumber, pageSize);
        return blogService.getAllBlogs(pageNumber, pageSize);
    }

    @GetMapping("/blog/{id}")
    public Mono<Blog> getBlogById(@PathVariable String id, ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/blog/{}'", firstIp, id);

        return blogService.findBlogById(id);
    }

    @PostMapping("/add-blog")
    public ResponseEntity<Mono<String>> addNewBlog(@RequestBody Blog blog, ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/add-blog'.", firstIp);
        Mono<String> response = blogService.addNewBlog(blog);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog")
    public ResponseEntity<Mono<String>> updateBlog(@RequestBody Blog blog, ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/update-blog'.", firstIp);
        Mono<String> response = blogService.updateBlog(blog);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}")
    public ResponseEntity<Mono<String>> deleteBlog(@PathVariable String blogId, ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/delete-blog/{}'.", firstIp, blogId);
        Mono<String> res = blogService.deleteBlog(blogId); // will throw NOT_FOUND if not found
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search-blogs/{prompt}")
    public Flux<Blog> search(@PathVariable String prompt, ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/search-blogs/{}'.", firstIp, prompt);
        return blogService.search(prompt);
    }


}
