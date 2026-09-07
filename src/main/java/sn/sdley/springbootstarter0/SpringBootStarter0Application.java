package sn.sdley.springbootstarter0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sn.sdley.springbootstarter0.entities.Address;
import sn.sdley.springbootstarter0.entities.User;
import sn.sdley.springbootstarter0.service.OrderService;
import sn.sdley.springbootstarter0.service.PayPalPaymentService;
import sn.sdley.springbootstarter0.service.StripePaymentService;

@SpringBootApplication
public class SpringBootStarter0Application {

    public static void main(String[] args) {
//        ApplicationContext context = SpringApplication.run(SpringBootStarter0Application.class, args);
        // Creating a new with builder pattern
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        var address = Address.builder()
                .street("123 Main St")
                .city("Anytown")
                .state("CA")
                .zipCode("12345")
                .build();

        user.addAddress(address);
        System.out.println(user);
    }

}
