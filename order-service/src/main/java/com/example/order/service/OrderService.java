package com.example.order.service;

import com.example.order.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> findAll();

    Order findById(Long id);

    List<Order> findByUserId(Long userId);

    Order create(Order order);

    Order update(Long id, Order order);

    void deleteById(Long id);
}
