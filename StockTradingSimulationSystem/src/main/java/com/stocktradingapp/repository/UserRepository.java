package com.stocktradingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stocktradingapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
}
