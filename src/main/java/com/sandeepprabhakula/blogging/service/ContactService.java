package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.repository.ReactiveContactRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final Logger log = LoggerFactory.getLogger(ContactService.class);
    private final ReactiveContactRepository contactRepository;
    public Mono<ResponseEntity<Map<String,Object>>> addComment(Contact contact){
        return contactRepository.save(contact)
                .flatMap(user->{
                    Map<String,Object>resp = new HashMap<>();
                    resp.put("status", HttpStatus.CREATED.value());
                    resp.put("message","Comment Uploaded.");
                    return Mono.just(new ResponseEntity<>(resp,HttpStatus.CREATED));
                })
                .onErrorResume(e->{
                    Map<String,Object>err = new HashMap<>();
                    err.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    err.put("message",e.getMessage());
                    return Mono.just(new ResponseEntity<>(err,HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    public Flux<Contact> getAllComments() {
        return Flux.defer(() -> {
                    long startTime = System.currentTimeMillis();
                    return contactRepository.findAll()
                            .doOnTerminate(() -> {
                                long duration = System.currentTimeMillis() - startTime;
                                log.info("Contact fetch completed in {} ms", duration);
                            });
                })
                .onErrorResume(e -> {
                    log.error("Database error while fetching contacts", e);
                    return Flux.error(new RuntimeException("Unable to retrieve contacts at this time"));
                });
    }

    public Mono<Contact> getCommentById(String id){
//        return contactRepository.findById(id).get();
        return contactRepository.findById(id)
                .flatMap(comment-> Mono.just(Objects.requireNonNullElseGet(comment, Contact::new)));
    }
}
