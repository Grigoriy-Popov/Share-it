package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User addUser(User user);

    User getUserById(Long userId);

    User updateUser(User user, Long userId);

    void deleteUser(Long userId);
}
