package ru.netology.FinalCloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import ru.netology.FinalCloud.Users.service.UserService;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.netology.FinalCloud.Users", "ru.netology.FinalCloud.FileCloud"})

public class FinalCloudApplication {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static void main(String[] args) {
        SpringApplication.run(FinalCloudApplication.class, args);
        logger.info("Application started");
    }
}
