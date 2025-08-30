package com.sandeepprabhakula.blogging.repository;

import com.sandeepprabhakula.blogging.data.Contact;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveContactRepository extends ReactiveMongoRepository<Contact,String> {
}
