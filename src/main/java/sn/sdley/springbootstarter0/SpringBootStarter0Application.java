package sn.sdley.springbootstarter0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sn.sdley.springbootstarter0.service.OrderService;
import sn.sdley.springbootstarter0.service.PayPalPaymentService;
import sn.sdley.springbootstarter0.service.StripePaymentService;

@SpringBootApplication
public class SpringBootStarter0Application {

    public static void main(String[] args) {
//        SpringApplication.run(SpringBootStarter0Application.class, args);
        var orderService = new OrderService();
//        orderService.setPaymentService(new PayPalPaymentService()); // uncomment this line to avoid NullPointerException in case
//                                                                      // you switched to setter injection...
        orderService.placeOrder();
    }

}
