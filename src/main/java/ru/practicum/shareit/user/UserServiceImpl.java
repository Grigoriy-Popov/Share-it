package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User addUser(User user) {
        validateCreationOfUser(user);
        return userRepository.addUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
    }

    @Override
    public User updateUser(User user, Long userId) {
        getUserById(userId);
        checkDuplicateEmail(user);
        user.setId(userId);
        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private void validateCreationOfUser(User user) {
        if (user.getEmail() == null) {
            log.warn("Failed validation: empty email");
            throw new ValidationException("Email cant be empty");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            log.warn("Failed validation: invalid email format - {}", user.getEmail());
            throw new ValidationException("Invalid email format");
        }
        checkDuplicateEmail(user);
    }

    private void checkDuplicateEmail(User user) {
        if (user.getEmail() != null) {
            for (User iterableUser : getAllUsers()) {
                if (user.getEmail().equals(iterableUser.getEmail())) {
                    log.warn("Failed validation: email {} is already in use", user.getEmail());
                    throw new DuplicateEmailException("This email is already in use");
                }
            }
        }
    }
}
