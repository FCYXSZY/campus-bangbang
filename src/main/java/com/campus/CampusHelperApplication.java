package com.campus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.mapper") // 加上这一行，扫描 mapper 包
public class CampusHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusHelperApplication.class, args);
    }

}
