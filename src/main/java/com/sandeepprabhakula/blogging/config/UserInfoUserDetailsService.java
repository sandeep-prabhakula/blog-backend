package com.sandeepprabhakula.blogging.config;

import com.sandeepprabhakula.blogging.repository.ReactiveUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserInfoUserDetailsService implements ReactiveUserDetailsService {
//    @Autowired
//    private UserRepository userRepository;
    @Autowired
    private ReactiveUserRepository userRepository;
//    @Override
//    public Mono<UserDetails> loadUserByUsername(String username) throws UsernameNotFoundException {
////        Optional<User>user = userRepository.findByEmail(username);
////        return user.map(UserInfoUserDetails::new).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
//        return userRepository.findByEmail(username)
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
//                .map(UserInfoUserDetails::new);
//    }

@Override
public Mono<UserDetails> findByUsername(String username) {

    return userRepository.findByEmail(username)
            .doOnNext(user -> System.out.println("Found user: " + user.getEmail()))
            .doOnError(error -> System.out.println("Error finding user: " + error.getMessage()))
            .switchIfEmpty(Mono.defer(() -> {
                System.out.println("User not found: " + username);
                return Mono.error(new UsernameNotFoundException("User not found"));
            }))
            .map(UserInfoUserDetails::new);
}
}
