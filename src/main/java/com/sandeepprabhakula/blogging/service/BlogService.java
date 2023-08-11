package com.sandeepprabhakula.blogging.service;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    private final MongoClient mongoClient;

    private final MongoConverter converter;

    public List<Blog> getAllBlogs(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC,"_id"));
        Page<Blog> page = blogRepository.findAll(pageable);
        return page.getContent();
    }

    public Blog findBlogById(String id){
        Optional<Blog> findingBlog = blogRepository.findById(id);
        return findingBlog.orElse(null);
    }
    public String addNewBlog(Blog blog) {
        blogRepository.save(blog);
        return "Blog Uploaded Successfully!";
    }


    public String updateBlog(Blog blog){
        Optional<Blog> findingBlog = blogRepository.findById(blog.getId());
        if(findingBlog.isEmpty())return "Blog not found";
        Blog currentBlog = findingBlog.get();
        currentBlog.setTitle(blog.getTitle());
        currentBlog.setDescription(blog.getDescription());
        currentBlog.setImage(blog.getImage());
        currentBlog.setPostedAt(blog.getPostedAt());
        currentBlog.setCode(blog.getCode());
        blogRepository.save(currentBlog);
        return "Blog Updated";
    }

    public String deleteBlog(String blogId){
        Optional<Blog> currentBlog = blogRepository.findById(blogId);
        if(currentBlog.isEmpty())return "Blog not found";
        blogRepository.deleteById(blogId);
        return "Blog deleted";
    }

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
