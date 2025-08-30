package com.sandeepprabhakula.blogging.repository;

import com.sandeepprabhakula.blogging.data.Blog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveBlogRepository extends ReactiveMongoRepository<Blog,String> {
}
