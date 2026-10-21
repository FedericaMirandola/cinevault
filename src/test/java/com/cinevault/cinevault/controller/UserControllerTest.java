package com.cinevault.cinevault.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.cinevault.cinevault.exception.EmailAlreadyRegisteredException;
import com.cinevault.cinevault.handler.GlobalExceptionHandler;
import com.cinevault.cinevault.service.UserService;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @Autowired // crea già il MockMvc e nn serve più il metodo .standaloneSetup()
    private MockMvc mockMvc;

    @MockitoBean // annotazione moderna per sostituire un bean con un mock
    private UserService userService;

    @Test
    void register_shouldRespond201Created_whenNewUserIsRegistered() throws Exception {

        String json = """
                {
                    "email": "test@example.com",
                    "password": "pwd"
                }
                """;

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Nuovo utente registrato con successo!"));

    }

    @Test
    void register_shouldReturn400_whenFieldsAreNotValid() throws Exception {

        String json = """
                {
                    "email": "",
                    "password": "pwd"
                }
                """;

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(anyString(), anyString());
    }

    @Test
    void register_shouldThrowEmailAlreadyRegisteredException_whenEmailRegisteredAlreadyExists() throws Exception {

        String json = """
                {
                    "email": "already@registered.it",
                    "password": "pwd123"
                }
                """;

        when(userService.register("already@registered.it", "pwd123"))
                .thenThrow(new EmailAlreadyRegisteredException(
                        "La mail corrisponde ad utente già registrato!"));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string("La mail corrisponde ad utente già registrato!"));
    }

}
