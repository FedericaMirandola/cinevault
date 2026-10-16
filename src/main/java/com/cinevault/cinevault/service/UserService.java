package com.cinevault.cinevault.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinevault.cinevault.entity.Role;
import com.cinevault.cinevault.entity.User;
import com.cinevault.cinevault.repository.UserRepository;


@Service
public class UserService {

    // final impedisce che, dopo l'inizializzazione, qualcuno assegni a quei campi
    // un riferimento diverso
    private final UserRepository userRepo;

    private final PasswordEncoder pwdEncoder;

    public UserService(UserRepository repo, PasswordEncoder pwd) {

        this.userRepo = repo;
        this.pwdEncoder = pwd;

    }

    public User register(String email, String password) {

        // Step 1: verificare se l'email è già presente nel DB
        Optional<User> existingUser = userRepo.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("La mail corrisponde ad utente già registrato!");
        } 

        // Step 2: se la mail nn esiste allora codifica la pwd con PasswordEncoder
        String encodedPwd = pwdEncoder.encode(password);
        
        // Crea nuovo utente con ruolo USER (riga 48)
        User newUser = new User();

        newUser.setEmail(email);
        newUser.setPassword(encodedPwd);
        newUser.setRole(Role.USER);
        newUser.setEnabled(true);

        // Salva il nuovo utente col metodo del repo .save()
        return userRepo.save(newUser);

    }

}
