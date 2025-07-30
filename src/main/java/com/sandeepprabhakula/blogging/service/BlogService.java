package com.sandeepprabhakula.blogging.service;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    private final MongoClient mongoClient;

    private final MongoConverter converter;
    @Cacheable(cacheNames = {"paginatedCache"},key= "#pageNumber+'_'+#pageSize")
    public List<Blog> getAllBlogs(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC,"_id"));
        Page<Blog> page = blogRepository.findAll(pageable);
        return page.getContent();
    }
    @Cacheable(cacheNames = {"blogCache"},key = "#id")
    public Blog findBlogById(String id){
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found with ID: " + id));
    }

    public String addNewBlog(Blog blog) {
        try{
            Blog response = blogRepository.save(blog);
            return "Blog uploaded with id: "+response.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @CachePut(cacheNames = {"blogCache"},key = "#blog.id")
    public String updateBlog(Blog blog){
        Blog currentBlog = blogRepository.findById(blog.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found with ID: " + blog.getId()));
        currentBlog.setTitle(blog.getTitle());
        currentBlog.setDescription(blog.getDescription());
        currentBlog.setImage(blog.getImage());
        currentBlog.setPostedAt(blog.getPostedAt());
        blogRepository.save(currentBlog);
        return "Blog Updated with ID: "+blog.getId();
    }
    @CacheEvict(cacheNames = {"blogCache"},key = "#blogId")
    public void deleteBlog(String blogId){
        blogRepository.findById(blogId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found with ID: " + blogId));
        blogRepository.deleteById(blogId);
    }
    @Cacheable(cacheNames = {"searchedCache"},key="#prompt")
    public List<Blog> search(String prompt){
        MongoDatabase database = mongoClient.getDatabase("blog");
        MongoCollection<Document> collection = database.getCollection("blogs");
        List<Blog> blogs = new ArrayList<>();
        AggregateIterable<Document> result = collection.aggregate(Collections.singletonList(new Document("$search",
                new Document("index", "default")
                        .append("wildcard",
                                new Document("query", prompt + "*")
                                        .append("path", Arrays.asList("title", "description"))
                                        .append("allowAnalyzedField", true)))));
        result.forEach(doc -> blogs.add(converter.read(Blog.class, doc)));
        return blogs;
    }

}
