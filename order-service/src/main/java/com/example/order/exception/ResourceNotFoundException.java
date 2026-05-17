package com.example.order.exception;


// 自定义异常类
public class ResourceNotFoundException extends RuntimeException {

    // 默认构造函数
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // 带 cause
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


/**
 * @StandardException // ✨ 一行注解，自动生成包含 message、cause 在内的所有标准构造方法
   public class ResourceNotFoundException extends RuntimeException {
        // 里面一个字都不用写！
   }
 **/