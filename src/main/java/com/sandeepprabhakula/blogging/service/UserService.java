package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;

    public String createNewUser(User user){
        Optional<User> findingUser = userRepository.findByEmail(user.getEmail());

        if(findingUser.get()!=null)return "User already exists";
        userRepository.save(user);
        return "Account Creation Successful";
    }

}
