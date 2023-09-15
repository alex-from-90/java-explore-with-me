package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.util.OffsetBasedPageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User newUser) {
        User user = new User();
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));
    }

    @Override
    @Transactional
    public void deleteUserById(int userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(List<Integer> ids) {
        return userRepository.findAllById(ids);
    }
}
