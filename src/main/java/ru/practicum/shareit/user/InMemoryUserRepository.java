package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private Long id = 0L;
    private Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User editUser(User user) {
        User repoUser = users.get(user.getId());
        if (user.getName() != null) {
            repoUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            repoUser.setEmail(user.getEmail());
        }
        return repoUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }
}
