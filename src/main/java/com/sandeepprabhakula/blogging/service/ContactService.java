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

    public List<Contact> getAllComments(String uid){
        Optional<User> currentUser = userRepository.findById(uid);
        if(currentUser.isEmpty() || !currentUser.get().getRoles().equals("ROLE_ADMIN"))return new ArrayList<>();
        return contactRepository.findAll();
    }

    public Contact getCommentById(String id,String uid){
        Optional<User> currentUser = userRepository.findById(uid);
        if(currentUser.isEmpty() || !currentUser.get().getRoles().equals("ROLE_ADMIN"))return new Contact();
        return contactRepository.findById(id).get();
    }
}
