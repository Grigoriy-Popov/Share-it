package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH_USERS = "/users";

    User user = new User(1L, "testUser", "test@email.com");
    UserDto userDto = new UserDto(1L, "testUser", "test@email.com");

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(user);

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void createInvalidUser() throws Exception {
        UserDto emptyNameNameUserDto = new UserDto(1L, "", "test@email.com");
        UserDto invalidEmailUserDto = new UserDto(1L, "testUser", "testemail.com");
        UserDto emptyEmailUserDto = new UserDto(1L, "testUser", "testemail.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(emptyNameNameUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(invalidEmailUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(emptyEmailUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.editUser(any(), any()))
                .thenReturn(user);

        mvc.perform(patch(BASE_PATH_USERS + "/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete(BASE_PATH_USERS + "/1"))
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1))
                .deleteUser(anyLong());
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(any()))
                .thenReturn(user);

        mvc.perform(get(BASE_PATH_USERS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(user));

        mvc.perform(get(BASE_PATH_USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())));
    }
}