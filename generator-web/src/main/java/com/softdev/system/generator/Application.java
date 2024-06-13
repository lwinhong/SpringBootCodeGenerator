package com.softdev.system.generator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhengkai.blog.csdn.net
 */
@SpringBootApplication
@MapperScan("com.softdev.system.generator.mapper")
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
