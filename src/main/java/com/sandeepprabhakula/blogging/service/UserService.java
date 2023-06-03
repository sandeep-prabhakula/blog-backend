package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.dto.LoginDTO;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;

    public String createNewUser(User user){
        User findingUser = userRepository.findByEmail(user.getEmail());

        if(findingUser!=null)return "User already exists";
        userRepository.save(user);
        return "Account Creation Successful";
    }

    public User login(LoginDTO loginDTO){
        User findingUser = userRepository.findByEmail(loginDTO.getEmail());
        if(findingUser==null)return null;
        if(findingUser.getPassword().equals(loginDTO.getPassword()))return findingUser;
        return null;
    }
}
