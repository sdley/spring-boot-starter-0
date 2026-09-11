package sn.sdley.springbootstarter0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sn.sdley.springbootstarter0.repositories.UserRepository;

@SpringBootApplication
public class SpringBootStarter0Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarter0Application.class, args);
    }

}
