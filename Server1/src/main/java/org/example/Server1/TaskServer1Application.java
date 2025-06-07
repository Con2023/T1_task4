package org.example.Server1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.Common", "org.example.Server1"})
public class TaskServer1Application {

	public static void main(String[] args) {
		SpringApplication.run(TaskServer1Application.class, args);
	}

}
