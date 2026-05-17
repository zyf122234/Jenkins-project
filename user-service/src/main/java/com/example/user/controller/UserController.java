package com.example.user.controller;

import com.example.user.entity.User;
import com.example.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        log.info("收到请求: 查询所有用户");
        List<User> users = userService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", users);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        log.info("收到请求: 查询用户, id={}", id);
        User user = userService.findById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody User user) {
        log.info("收到请求: 创建用户, username={}", user.getUsername());
        User created = userService.create(user);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "用户创建成功");
        result.put("data", created);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody User user) {
        log.info("收到请求: 更新用户, id={}", id);
        User updated = userService.update(id, user);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "用户更新成功");
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        log.info("收到请求: 删除用户, id={}", id);
        userService.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "用户删除成功");
        result.put("data", null);
        return ResponseEntity.ok(result);
    }
}
