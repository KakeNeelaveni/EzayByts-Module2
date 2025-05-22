package com.stocktradingapp.service;

import org.springframework.http.ResponseEntity;

import com.stocktradingapp.entity.User;

public interface UserService {
    boolean existsByUsername(String username);
    void save(User user);
    User findByUsername(String username);
    ResponseEntity<String> register(User user);
    ResponseEntity<String> login(User user);
}
