package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.service.ContactService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/get-all-comments/{uid}")
    public List<Contact> getAllComments(@PathVariable("uid") String uid) {
        return contactService.getAllComments(uid);
    }

    @GetMapping("/comment/{id}/{uid}")
    public Contact getCommentById(@PathVariable("id") String id, @PathVariable("uid") String uid) {
        return contactService.getCommentById(id,uid);
    }
}
