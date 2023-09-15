package ru.practicum.mainservice.service;

import ru.practicum.mainservice.model.User;

import java.util.List;

public interface UserService {
    User createUser(User newUser);

    User getUserById(int userId);

    void deleteUserById(int userId);

    List<User> getUsers(int from, int size);

    List<User> getUsers(List<Integer> ids);
}
