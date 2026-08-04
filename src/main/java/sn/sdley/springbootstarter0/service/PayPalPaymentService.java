package sn.sdley.springbootstarter0.service;

import org.springframework.stereotype.Service;

@Service
public class PayPalPaymentService implements PaymentService {
    @Override
    public void processPayment(double amount) {
        System.out.println("Initiating payment processing through PayPal...");
        System.out.println("Processing payment of $" + amount + " through PayPal.");
        System.out.println("Payment processed successfully.");
    }
}
