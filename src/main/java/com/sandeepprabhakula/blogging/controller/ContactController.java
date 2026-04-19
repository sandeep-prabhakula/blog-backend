package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.service.ContactService;
import com.sandeepprabhakula.blogging.util.AppUtilities;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContactController {
    private final ContactService contactService;
    private final AppUtilities appUtilities;
    private final Logger log = LoggerFactory.getLogger(ContactController.class);
    @PostMapping("/add-comment")
    public Mono<ResponseEntity<Map<String,Object>>> addComment(@RequestBody Contact contact, ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/add-comment", firstIp);
        return contactService.addComment(contact);
    }

    @GetMapping("/get-all-comments")
    public Flux<Contact> getAllComments(ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/get-all-comments", firstIp);
        return contactService.getAllComments();
    }

    @GetMapping("/comment/{id}")
    public Mono<Contact> getCommentById(@PathVariable String id,ServerHttpRequest request) {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/comment/{}", firstIp,id);
        return contactService.getCommentById(id);
    }
}
