package com.example.user.service.impl;

import com.example.user.entity.User;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findAll() {
        log.info("查询所有用户");
        return userMapper.findAll();
    }

    @Override
    public User findById(Long id) {
        log.info("查询用户, id={}", id);
        User user = userMapper.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在, id=" + id);
        }
        return user;
    }

    @Override
    public User create(User user) {
        log.info("创建用户: {}", user.getUsername());
        userMapper.insert(user);
        log.info("用户创建成功, id={}", user.getId());
        return user;
    }

    @Override
    public User update(Long id, User user) {
        log.info("更新用户, id={}", id);
        User existing = userMapper.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("用户不存在, id=" + id);
        }
        user.setId(id);
        userMapper.update(user);
        return userMapper.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("删除用户, id={}", id);
        User existing = userMapper.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("用户不存在, id=" + id);
        }
        userMapper.deleteById(id);
    }
}
