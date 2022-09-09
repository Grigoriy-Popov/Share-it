package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    void createUser() {
        var userToCreate = new User(null, "testUser", "test@email.com");
        userService.createUser(userToCreate);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userToCreate.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userToCreate.getName()));
        assertThat(user.getEmail(), equalTo(userToCreate.getEmail()));
    }

    @Test
    void getUserById() {
        var userToCreate = new User(null, "testUser", "test@email.com");
        userService.createUser(userToCreate);

        User user = userService.getUserById(1L);

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userToCreate.getName()));
        assertThat(user.getEmail(), equalTo(userToCreate.getEmail()));
        assertThat(user, equalTo(userToCreate));
    }

    @Test
    void getAllUsers() {
        var userToCreate = new User(null, "testUser", "test@email.com");
        var userToCreate2 = new User(null, "testUser2", "test2@email.com");
        userService.createUser(userToCreate);
        userService.createUser(userToCreate2);

        var usersList = userService.getAllUsers();

        assertThat(usersList, hasSize(2));
        assertThat(usersList.get(0), equalTo(userToCreate));
        assertThat(usersList.get(1), equalTo(userToCreate2));
    }

    @Test
    void editUser() {
        var userToCreate = new User(null, "testUser", "test@email.com");
        var userToUpdate = new User(null, "testUser2", "test2@email.com");
        var userToUpdate2 = new User(null, null, "test3@email.com");
        var userToUpdate3 = new User(null, "testUser3", null);
        userService.createUser(userToCreate);

        var updatedUser = userService.editUser(userToUpdate, 1L);

        assertThat(updatedUser.getName(), equalTo(userToUpdate.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userToUpdate.getEmail()));

        userService.editUser(userToUpdate2, 1L);

        assertThat(updatedUser.getEmail(), equalTo(userToUpdate2.getEmail()));

        userService.editUser(userToUpdate3, 1L);

        assertThat(updatedUser.getName(), equalTo(userToUpdate3.getName()));
    }

    @Test
    void deleteUser() {
        var userToCreate = new User(null, "testUser", "test@email.com");
        userService.createUser(userToCreate);

        userService.deleteUser(1L);

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertThat(e.getMessage(), equalTo("User with id 1 not found"));
    }
}
