package com.sandeepprabhakula.blogging.repository;

import com.sandeepprabhakula.blogging.data.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact,String> {
}
