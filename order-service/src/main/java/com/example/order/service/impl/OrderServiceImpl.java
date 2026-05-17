package com.example.order.service.impl;

import com.example.order.entity.Order;
import com.example.order.exception.ResourceNotFoundException;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> findAll() {
        log.info("查询所有订单");
        return orderMapper.findAll();
    }

    @Override
    public Order findById(Long id) {
        log.info("查询订单, id={}", id);
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new ResourceNotFoundException("订单不存在, id=" + id);
        }
        return order;
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        log.info("查询用户订单, userId={}", userId);
        return orderMapper.findByUserId(userId);
    }

    @Override
    public Order create(Order order) {
        log.info("创建订单: userId={}, productName={}", order.getUserId(), order.getProductName());
        if (order.getStatus() == null) {
            order.setStatus("CREATED");
        }
        orderMapper.insert(order);
        log.info("订单创建成功, id={}", order.getId());
        return order;
    }

    @Override
    public Order update(Long id, Order order) {
        log.info("更新订单, id={}", id);
        Order existing = orderMapper.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("订单不存在, id=" + id);
        }
        order.setId(id);
        orderMapper.update(order);
        return orderMapper.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("删除订单, id={}", id);
        Order existing = orderMapper.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("订单不存在, id=" + id);
        }
        orderMapper.deleteById(id);
    }
}
