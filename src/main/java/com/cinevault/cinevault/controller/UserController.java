package com.cinevault.cinevault.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinevault.cinevault.dto.RegisterRequest;
import com.cinevault.cinevault.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request ) {
        
        String email=  request.getEmail();
        String password = request.getPassword();

        userService.register(email, password);

        return ResponseEntity.status(HttpStatus.CREATED).body("Nuovo utente registrato con successo!");
    }

}
