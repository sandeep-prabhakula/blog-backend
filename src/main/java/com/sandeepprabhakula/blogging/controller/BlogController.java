package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.service.BlogService;
import lombok.RequiredArgsConstructor;
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


    @GetMapping("/get-all-blogs")
    public List<Blog> getAllBlogs(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5",required = false)int pageSize
            ){
        return blogService.getAllBlogs(pageNumber,pageSize);
    }

    @GetMapping("/blog/{id}")
    public Blog getBlogById(@PathVariable("id")String id){
        return blogService.findBlogById(id);
    }

    @PostMapping("/add-blog")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addNewBlog(@RequestBody Blog blog){
        String response = blogService.addNewBlog(blog);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateBlog(@RequestBody Blog blog, @PathVariable("id")String id){
        String response = blogService.updateBlog(blog,id);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBlog(@PathVariable("blogId")String blogId,@PathVariable("id")String id){
        String response = blogService.deleteBlog(blogId, id);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-blogs/{prompt}")
    public List<Blog> search(@PathVariable("prompt")String prompt){
        return blogService.search(prompt);
    }

}
