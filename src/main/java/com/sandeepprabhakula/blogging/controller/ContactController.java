package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContactController {
    private final ContactService contactService;

    @PostMapping("/add-comment")
    public Mono<ResponseEntity<Map<String,Object>>> addComment(@RequestBody Contact contact) {
        return contactService.addComment(contact);
    }

    @GetMapping("/get-all-comments")
    public Flux<Contact> getAllComments() {
        return contactService.getAllComments();
    }

    @GetMapping("/comment/{id}")
    public Mono<Contact> getCommentById(@PathVariable("id") String id) {
        return contactService.getCommentById(id);
    }
}
