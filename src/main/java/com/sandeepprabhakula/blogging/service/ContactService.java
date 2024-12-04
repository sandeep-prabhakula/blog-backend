package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;

    public void addComment(Contact contact){
        contactRepository.save(contact);
    }

    public List<Contact> getAllComments(){
        return contactRepository.findAll();
    }

    public Contact getCommentById(String id){
        return contactRepository.findById(id).get();
    }
}
