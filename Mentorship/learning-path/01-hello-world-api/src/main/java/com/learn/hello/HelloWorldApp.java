package com.learn.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication does 3 things in one annotation:
//   1. @Configuration      — this class can define Spring beans
//   2. @EnableAutoConfiguration — Spring Boot auto-configures everything it finds on the classpath
//                                 (e.g., it sees spring-boot-starter-web → starts a web server)
//   3. @ComponentScan      — Spring scans this package and all sub-packages for
//                            @RestController, @Service, @Repository etc.
@SpringBootApplication
public class HelloWorldApp {

    public static void main(String[] args) {
        // This one line starts the embedded Tomcat server and your entire application.
        // You do NOT need to install Tomcat — Spring Boot bundles it for you.
        SpringApplication.run(HelloWorldApp.class, args);
    }
}

