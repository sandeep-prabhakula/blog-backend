package com.sandeepprabhakula.blogging.repository;

import com.sandeepprabhakula.blogging.data.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveUserRepository extends ReactiveMongoRepository<User,String> {
    Mono<User>findByEmail(String email);
}
