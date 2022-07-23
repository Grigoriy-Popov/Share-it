package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAllUsers();

    User addUser(User user);

    Optional<User> getUserById(Long userId);

    User updateUser(User user);

    void deleteUser(Long userId);
}
