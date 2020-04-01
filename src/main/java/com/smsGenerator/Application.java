package com.smsGenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class Application {

    public static void main(String[] args) {
        String prop = System.getProperty("some.address");
        System.out.println(prop);
        SpringApplication.run(Application.class, args);
    }
}
