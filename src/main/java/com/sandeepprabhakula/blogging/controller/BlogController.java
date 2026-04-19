package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class BlogController {

    private final BlogService blogService;
    private String getClientIPAddr(ServerHttpRequest request){
        String userIp = request.getHeaders().getFirst("X-Forwarded-For");
        if (userIp == null || userIp.isEmpty()) {
            userIp = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
        }
        return userIp.split(",")[0].trim();
    }
    @GetMapping("/get-all-blogs")
    public Flux<Blog> getAllBlogs(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5",required = false)int pageSize,
            ServerHttpRequest request){
        String firstIp = getClientIPAddr(request);

        System.out.println("Request from IP: " + firstIp+" to endpoint '/get-all-blogs"+"?pageNumber="+pageNumber+"&pageSize"+pageSize+"'.");
        return blogService.getAllBlogs(pageNumber,pageSize);
    }

    @GetMapping("/blog/{id}")
    public Mono<Blog> getBlogById(@PathVariable String id){

        return blogService.findBlogById(id);
    }

    @PostMapping("/add-blog")
    public ResponseEntity<Mono<String>> addNewBlog(@RequestBody Blog blog){

        Mono<String> response = blogService.addNewBlog(blog);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-blog")
    public ResponseEntity<Mono<String>> updateBlog(@RequestBody Blog blog){

        Mono<String> response = blogService.updateBlog(blog);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-blog/{blogId}")
    public ResponseEntity<Mono<String>> deleteBlog(@PathVariable String blogId){

        Mono<String> res = blogService.deleteBlog(blogId); // will throw NOT_FOUND if not found
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search-blogs/{prompt}")
    public Flux<Blog> search(@PathVariable String prompt){

        return blogService.search(prompt);
    }





}
