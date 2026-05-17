package com.example.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

//这是一个函数式接口，函数式接口，就是只有一个抽象方法，其实现类可以使用lambda表达式进行实现（匿名类）
//你在接口上打上添加@FeignClient(name = "user-service") ，OpenFeign 会在底层默默和 Nacos 打配合，会去nacos中查找已经注册的用户服务，并调用该服务，如果用户服务不可用，则返回 fallbackFactory 指定的 fallback (降级策略)
// 用户服务 Feign 客户端，默认情况下，FeignClient 会将请求转发给用户服务，如果用户服务不可用，则返回 fallbackFactory 指定的 fallback (降级策略)
@FeignClient(name = "user-service", fallbackFactory = UserFeignClientFallbackFactory.class)
public interface UserFeignClient {

    // 获取用户信息
    @GetMapping("/api/users/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);
}
