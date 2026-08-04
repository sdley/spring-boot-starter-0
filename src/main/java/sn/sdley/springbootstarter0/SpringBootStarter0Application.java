package sn.sdley.springbootstarter0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sn.sdley.springbootstarter0.service.OrderService;
import sn.sdley.springbootstarter0.service.PayPalPaymentService;
import sn.sdley.springbootstarter0.service.StripePaymentService;

@SpringBootApplication
public class SpringBootStarter0Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootStarter0Application.class, args);
        var orderService = context.getBean(OrderService.class);
        orderService.placeOrder();
    }

}
