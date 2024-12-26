package com.hiss.avalor_backend;

import com.hiss.avalor_backend.entity.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
//@EnableScheduling
public class AvalorBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvalorBackendApplication.class, args);
	}

}
