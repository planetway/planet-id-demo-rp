package com.planetway.fudosan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "jp.relyingparty", "com.planetway" })
public class RelyingPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelyingPartyApplication.class, args);
    }
}
