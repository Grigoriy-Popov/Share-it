package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        checkDuplicateEmail(user);
        return userRepository.addUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User editUser(User user, Long userId) {
        getUserById(userId); // Для проверки, что пользователь существует
        checkDuplicateEmail(user);
        user.setId(userId);
        return userRepository.editUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    // оставил пока не разберусь с выводом сообщений при некорректной валидации через аннотации
/*    private void validateCreationOfUser(User user) {
        if (user.getEmail() == null) {
            log.warn("Failed validation: empty email");
            throw new ValidationException("Email cant be empty");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            log.warn("Failed validation: invalid email format - {}", user.getEmail());
            throw new ValidationException("Invalid email format");
        }
        checkDuplicateEmail(user);
    }*/

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
