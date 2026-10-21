package com.cinevault.cinevault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cinevault.cinevault.entity.Role;
import com.cinevault.cinevault.entity.User;
import com.cinevault.cinevault.exception.EmailAlreadyRegisteredException;
import com.cinevault.cinevault.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepo;

    @Mock
    PasswordEncoder pwdEncoder;

    @Test
    void register_shouldSaveNewUser_IfEmailIsNotPresent() {

        when(userRepo.findByEmail("new@email.it")).thenReturn(Optional.empty());

        when(pwdEncoder.encode("MyPWD123")).thenReturn("encoded");

        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserService service = new UserService(userRepo, pwdEncoder);

        User result = service.register("new@email.it", "MyPWD123");

        assertEquals("new@email.it", result.getEmail());
        assertEquals("encoded", result.getPassword());
        assertEquals(Role.USER, result.getRole());
        assertEquals(true, result.isEnabled());

    }

    @Test
    void register_shouldThrowException_IfEmailIsPresent() {

        User existingUser = new User();
        existingUser.setEmail("new@email.it");
        existingUser.setPassword("pwd");
        existingUser.setRole(Role.USER);
        existingUser.setEnabled(true);

        when(userRepo.findByEmail("new@email.it")).thenReturn(Optional.of(existingUser));

        UserService service = new UserService(userRepo, pwdEncoder);

        assertThrows(EmailAlreadyRegisteredException.class, () -> {
            service.register("new@email.it", "pwd");
        });

        verify(userRepo, never()).save(any(User.class));

    }

}
