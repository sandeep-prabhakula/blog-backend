package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/add-blog/{id}")
    public ResponseEntity<String> addNewBlog(@RequestBody Blog blog, @PathVariable("id")String id){
        String response = blogService.addNewBlog(blog,id);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog/{id}")
    public ResponseEntity<String> updateBlog(@RequestBody Blog blog, @PathVariable("id")String id){
        String response = blogService.updateBlog(blog,id);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}/{id}")
    public ResponseEntity<String> deleteBlog(@PathVariable("blogId")String blogId,@PathVariable("id")String id){
        String response = blogService.deleteBlog(blogId, id);
        if(response.equals("401 Unauthorized"))return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        return ResponseEntity.ok(response);
    }

}
