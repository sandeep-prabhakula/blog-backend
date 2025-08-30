package com.sandeepprabhakula.blogging.service;


import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.repository.ReactiveBlogRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BlogService {

    //    private final BlogRepository blogRepository;
    private final ReactiveBlogRepository blogRepository;
//    private final MongoClient mongoClient;

    private final ReactiveMongoTemplate mongoTemplate;
    private final MongoConverter converter;

    @Cacheable(cacheNames = {"paginatedCache"}, key = "#pageNumber+'_'+#pageSize")
    public Flux<Blog> getAllBlogs(long pageNumber, long pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC,"_id"));
        return blogRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .skip(pageNumber * pageSize)
                .take(pageSize);
    }

    @Cacheable(cacheNames = {"blogCache"}, key = "#id")
    public Mono<Blog> findBlogById(String id) {
        return blogRepository.findById(id)
                .onErrorMap(throwable -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found with ID: " + id));
    }

    @CacheEvict(cacheNames = {"paginatedCache"},allEntries = true)
    public Mono<String> addNewBlog(Blog blog) {
        try {
            return blogRepository.save(blog)
                    .flatMap(response -> Mono.just("Blog uploaded with id: " + response.getId()))
                    .onErrorMap(err -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @CacheEvict(cacheNames = {"blogCache","paginatedCache"},allEntries = true, key = "#blogId")
    public Mono<String> updateBlog(Blog blog) {
        return blogRepository.findById(blog.getId())
                .flatMap(existingBlog -> {
                    // ✅ UPDATE the existing blog with new data
                    existingBlog.setTitle(blog.getTitle());
                    existingBlog.setDescription(blog.getDescription());
                    existingBlog.setImage(blog.getImage());
                    existingBlog.setPostedAt(blog.getPostedAt()); // ✅ Update timestamp

                    return blogRepository.save(existingBlog)
                            .flatMap(updatedBlog -> {
                                System.out.println("Blog updated: " + updatedBlog.getTitle());
                                return Mono.just("Blog updated with id: " + updatedBlog.getId());
                            });
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof ResponseStatusException) {
                        return Mono.error(throwable);
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found with ID: " + blog.getId()));
                })
                .onErrorResume(throwable -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update blog: " + throwable.getMessage())));
    }

    @CacheEvict(cacheNames = {"blogCache","paginatedCache"},allEntries = true, key = "#blogId")
    public Mono<String> deleteBlog(String blogId) {
        return blogRepository.findById(blogId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found with ID: " + blogId)))
                .flatMap(blog -> blogRepository.deleteById(blogId))
                .thenReturn("Blog deleted with ID: " + blogId)
                .onErrorResume(throwable -> {
                    if (throwable instanceof ResponseStatusException) {
                        return Mono.error(throwable);
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete failed: " + throwable.getMessage()));
                });
    }

    @Cacheable(cacheNames = {"searchedCache"}, key = "#prompt")
    public Flux<Blog> search(String prompt) {

        Document searchStage = new Document("$search",
                new Document("index", "default")
                        .append("wildcard",
                                new Document("query", prompt + "*")
                                        .append("path", Arrays.asList("title", "description"))
                                        .append("allowAnalyzedField", true)));

        TypedAggregation<Blog> aggregation = Aggregation.newAggregation(
                Blog.class,
                context -> searchStage
        );

        // Execute the reactive aggregation
        return mongoTemplate.aggregate(aggregation, Blog.class);

    }

}
