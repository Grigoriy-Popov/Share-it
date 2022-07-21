package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    User addUser(User user);

    Optional<User> getUserById(Long userId);
}
