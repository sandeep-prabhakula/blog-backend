package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.Contact;
import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.ContactRepository;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
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
