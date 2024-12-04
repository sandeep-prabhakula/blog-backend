package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContactController {
    private final ContactService contactService;

    @PostMapping("/add-comment")
    public void addComment(@RequestBody Contact contact) {
        contactService.addComment(contact);
    }

    @GetMapping("/get-all-comments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Contact> getAllComments() {
        return contactService.getAllComments();
    }

    @GetMapping("/comment/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Contact getCommentById(@PathVariable("id") String id) {
        return contactService.getCommentById(id);
    }
}
