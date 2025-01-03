package com.wzk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.wzk.rjcg.mapper")
@ComponentScan("com.wzk.rjcg")
public class RjcgApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RjcgApplication.class, args);
	}
	
}
