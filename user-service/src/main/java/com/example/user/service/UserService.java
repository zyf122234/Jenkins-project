package com.example.user.service;

import com.example.user.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(Long id, User user);

    void deleteById(Long id);
}
