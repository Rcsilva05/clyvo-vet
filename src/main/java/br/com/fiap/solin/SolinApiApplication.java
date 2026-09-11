package br.com.fiap.solin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SolinApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolinApiApplication.class, args);
    }
}
