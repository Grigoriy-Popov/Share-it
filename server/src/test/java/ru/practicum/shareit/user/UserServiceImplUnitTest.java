package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository repository;

    User user = new User(1L, "testUser", "test@email.com");
    User user2 = new User(2L, "testUser2", "test2@email.com");

    @Test
    public void createUser_shouldReturnUserWhenCreateUser() {
        when(repository.save(any()))
                .thenReturn(user);

        assertThat(userService.createUser(user), equalTo(user));
    }

    @Test
    public void getUserById_shouldReturnUserWhenUserExist() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "testUser", "test@email.com")));

        assertThat(user, equalTo(userService.getUserById(1L)));
    }

    @Test
    public void getUserById_shouldThrowNotFoundExceptionWhenUserNotExist() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertThat(e.getMessage(), equalTo("User with id 1 not found"));
    }

    @Test
    public void getAllUsers_shouldReturnUsersListWhenUsersExists() {
        when(repository.findAll())
                .thenReturn(List.of(user, user2));

        List<User> users = userService.getAllUsers();
        assertThat(user, equalTo(users.get(0)));
        assertThat(user2, equalTo(users.get(1)));
        assertThat(users, hasSize(2));
    }

    @Test
    public void getAllUsers_shouldReturnEmptyListWhenUsersNotExists() {
        when(repository.findAll())
                .thenReturn(new ArrayList<>());

        List<User> users = userService.getAllUsers();
        assertThat(users, is(empty()));
    }

    @Test
    public void editUser_shouldUpdateUserAndReturnThisUserWhenUserExists() {
        User userToUpdate = new User(1L, "testUpdateUser", "testUpdateUser@email.com");
        User updateUser = new User(1L, "testUpdateUser1", "testUpdateUser1@email.com");

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(userToUpdate));

        when(repository.save(any()))
                .thenReturn(userToUpdate);

        User updatedUser = userService.editUser(updateUser, 1L);
        assertThat(updatedUser, equalTo(userToUpdate));
    }

    @Test
    public void editUser_shouldThrowNotFoundExceptionWhenUserNotExists() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertThat(e.getMessage(), equalTo("User with id 1 not found"));
    }

    @Test
    public void editUser_shouldUpdateUsersNameAndReturnThisUserWhenUserExists() {
        User userToUpdate = new User(1L, "testUpdateUser", "testUpdateUser@email.com");
        User updateUser = new User(1L, "testUpdateUser1", null);

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(userToUpdate));

        when(repository.save(any()))
                .thenReturn(userToUpdate);

        User updatedUser = userService.editUser(updateUser, 1L);
        assertThat(updatedUser.getName(), equalTo(userToUpdate.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userToUpdate.getEmail()));
    }

    @Test
    public void editUser_shouldUpdateUsersEmailAndReturnThisUserWhenUserExistsInRepo() {
        User userToUpdate = new User(1L, "testUpdateUser", "testUpdateUser@email.com");
        User updateUser = new User(1L, null, "testUpdateUser1@email.com");

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(userToUpdate));

        when(repository.save(any()))
                .thenReturn(userToUpdate);

        User updatedUser = userService.editUser(updateUser, 1L);
        assertThat(updatedUser.getEmail(), equalTo(userToUpdate.getEmail()));
        assertThat(updatedUser.getName(), equalTo(userToUpdate.getName()));
    }

    @Test
    public void deleteUser_shouldDeleteUser() {
        userService.deleteUser(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
    }
}