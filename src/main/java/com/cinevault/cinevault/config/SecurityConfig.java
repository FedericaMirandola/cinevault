package com.cinevault.cinevault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean 
    public PasswordEncoder createPwdEncoder () {

        BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

        return pwdEncoder;
    }
    
}
