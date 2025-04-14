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

import java.util.List;

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
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateBlog(@RequestBody Blog blog){
        getIPAddress("update-blog");
        String response = blogService.updateBlog(blog);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBlog(@PathVariable("blogId")String blogId){
        getIPAddress("delete-blog");
        String response = blogService.deleteBlog(blogId);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
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

}
