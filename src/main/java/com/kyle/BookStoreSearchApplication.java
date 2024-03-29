package com.kyle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class BookStoreSearchApplication {
    public static void main(String[] args){
        SpringApplication.run(BookStoreSearchApplication.class);
    }
}
