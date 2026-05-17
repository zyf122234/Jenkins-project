package com.example.order.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    private static final Logger log = LoggerFactory.getLogger(UserFeignClientFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("调用 user-service 失败: {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public Map<String, Object> getUserById(Long id) {
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("code", 503);
                fallback.put("message", "用户服务暂时不可用");
                fallback.put("data", null);
                return fallback;
            }
        };
    }
}
