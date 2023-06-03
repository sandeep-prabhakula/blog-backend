package com.sandeepprabhakula.blogging.repository;

import com.sandeepprabhakula.blogging.data.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends MongoRepository<Blog,String> {

}
