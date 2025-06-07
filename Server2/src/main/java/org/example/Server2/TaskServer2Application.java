package org.example.Server2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.Common", "org.example.Server2"})
public class TaskServer2Application {

    public static void main(String[] args) {
        SpringApplication.run(TaskServer2Application.class, args);
    }

}