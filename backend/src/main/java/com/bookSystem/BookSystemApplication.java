package com.bookSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 图书借阅与销售平台 - 启动类
 *
 * @author BookSystem Team
 */
@SpringBootApplication
public class BookSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookSystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("  图书借阅与销售平台 启动成功！");
        System.out.println("  API 文档: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
