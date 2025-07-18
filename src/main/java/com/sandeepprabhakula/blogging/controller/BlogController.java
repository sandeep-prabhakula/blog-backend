package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class BlogController {

    private final BlogService blogService;
    private final HttpServletRequest request;
    private final Logger log = LoggerFactory.getLogger(BlogController.class);

    @GetMapping("/get-all-blogs")
    public List<Blog> getAllBlogs(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5",required = false)int pageSize
            ){

        getIPAddress("get-all-blogs");
        return blogService.getAllBlogs(pageNumber,pageSize);
    }

    @GetMapping("/blog/{id}")
    public Blog getBlogById(@PathVariable("id")String id){
        getIPAddress("blog");
        return blogService.findBlogById(id);
    }

    @PostMapping("/add-blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addNewBlog(@RequestBody Blog blog){
        getIPAddress("add-blog");
        String response = blogService.addNewBlog(blog);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateBlog(@RequestBody Blog blog){
        getIPAddress("update-blog");
        String response = blogService.updateBlog(blog);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBlog(@PathVariable("blogId")String blogId){
        getIPAddress("delete-blog");
        blogService.deleteBlog(blogId); // will throw NOT_FOUND if not found
        return ResponseEntity.ok("Blog deleted with ID: " + blogId);
    }

    @GetMapping("/search-blogs/{prompt}")
    public List<Blog> search(@PathVariable("prompt")String prompt){
        getIPAddress("search-blogs");
        return blogService.search(prompt);
    }

    private void getIPAddress(String endpoint){
        try {
            log.info("{} accessed from ip addr: {}", endpoint,request.getRemoteAddr());
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
