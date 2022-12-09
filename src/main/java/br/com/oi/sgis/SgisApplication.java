package br.com.oi.sgis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SgisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SgisApplication.class, args);
	}

}
