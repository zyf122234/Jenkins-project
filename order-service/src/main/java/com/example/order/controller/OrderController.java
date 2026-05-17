package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.feign.UserFeignClient;
import com.example.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        log.info("收到请求: 查询所有订单");
        List<Order> orders = orderService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", orders);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        log.info("收到请求: 查询订单, id={}", id);
        Order order = orderService.findById(id);

        // 通过 Feign 调用 user-service 获取用户信息
        Map<String, Object> userInfo = userFeignClient.getUserById(order.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("user", userInfo.get("data"));
        result.put("data", data);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> findByUserId(@PathVariable Long userId) {
        log.info("收到请求: 查询用户订单, userId={}", userId);
        List<Order> orders = orderService.findByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", orders);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Order order) {
        log.info("收到请求: 创建订单, userId={}, productName={}", order.getUserId(), order.getProductName());

        // 验证用户是否存在
        Map<String, Object> userInfo = userFeignClient.getUserById(order.getUserId());
        if (userInfo.get("data") == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "用户不存在, userId=" + order.getUserId());
            result.put("data", null);
            return ResponseEntity.badRequest().body(result);
        }

        Order created = orderService.create(order);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "订单创建成功");
        result.put("data", created);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Order order) {
        log.info("收到请求: 更新订单, id={}", id);
        Order updated = orderService.update(id, order);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "订单更新成功");
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        log.info("收到请求: 删除订单, id={}", id);
        orderService.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "订单删除成功");
        result.put("data", null);
        return ResponseEntity.ok(result);
    }
}
