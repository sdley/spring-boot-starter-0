package sn.sdley.springbootstarter0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sn.sdley.springbootstarter0.entities.Address;
import sn.sdley.springbootstarter0.entities.Profile;
import sn.sdley.springbootstarter0.entities.Tag;
import sn.sdley.springbootstarter0.entities.User;
import sn.sdley.springbootstarter0.repositories.UserRepository;
import sn.sdley.springbootstarter0.service.OrderService;
import sn.sdley.springbootstarter0.service.PayPalPaymentService;
import sn.sdley.springbootstarter0.service.StripePaymentService;

@SpringBootApplication
public class SpringBootStarter0Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootStarter0Application.class, args);

        var repository = context.getBean(UserRepository.class);

        repository.findAll().forEach(u -> System.out.println(u.getName() + " - " + u.getEmail()));

    }

}
