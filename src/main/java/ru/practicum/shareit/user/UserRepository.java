package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User addUser(User user);

    Optional<User> getUserById(Long userId);

    Collection<User> getAllUsers();

    User editUser(User user);

    void deleteUser(Long userId);
}
