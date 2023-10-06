package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    public String createNewUser(User user){
        Optional<User> findingUser = userRepository.findByEmail(user.getEmail());

        if(findingUser.isPresent())return "User already exists";
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return "Account Creation Successful";
    }

}
