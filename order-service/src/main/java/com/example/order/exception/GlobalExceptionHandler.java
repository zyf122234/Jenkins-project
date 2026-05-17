package com.example.order.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 日志
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    //日志是有级别的（trace < debug < info < warn < error



    // 处理资源未找到的异常
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("资源未找到: {}", ex.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 404);
        result.put("message", ex.getMessage());
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    // 处理参数校验失败的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("参数校验失败: {}", ex.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", "参数校验失败: " + ex.getBindingResult().getFieldError().getDefaultMessage());
        result.put("data", null);
        return ResponseEntity.badRequest().body(result);
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("系统异常", ex);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "系统内部错误: " + ex.getMessage());
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
