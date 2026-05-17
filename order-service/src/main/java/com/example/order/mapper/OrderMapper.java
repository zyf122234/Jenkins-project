package com.example.order.mapper;

import com.example.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<Order> findAll();

    Order findById(@Param("id") Long id);

    List<Order> findByUserId(@Param("userId") Long userId);

    int insert(Order order);

    int update(Order order);

    int deleteById(@Param("id") Long id);
}
