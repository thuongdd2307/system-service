package com.example.systemserviceofficial;

import com.example.commonserviceofficial.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class
})
public class SystemServiceOfficialApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemServiceOfficialApplication.class, args);
	}

}
