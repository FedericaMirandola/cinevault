package com.cinevault.cinevault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinevault.cinevault.entity.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByEmail (String email);
    
}
