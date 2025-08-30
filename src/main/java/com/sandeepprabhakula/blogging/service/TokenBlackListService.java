package com.sandeepprabhakula.blogging.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class TokenBlackListService {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklistedTokens.add(token);
    }

    public Mono<Boolean> contains(String token) {
        return Mono.just(blacklistedTokens.contains(token));
    }
}
