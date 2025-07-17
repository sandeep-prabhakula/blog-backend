package com.sandeepprabhakula.blogging.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class TokenBlackListService {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklistedTokens.add(token);
    }

    public boolean contains(String token) {
        return blacklistedTokens.contains(token);
    }
}
