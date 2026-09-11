package sn.sdley.springbootstarter0.service;

import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.repositories.PaymentService;

@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        System.out.println("Placing order...");
        paymentService.processPayment(100.0);
    }

//    public void setPaymentService(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }
}
