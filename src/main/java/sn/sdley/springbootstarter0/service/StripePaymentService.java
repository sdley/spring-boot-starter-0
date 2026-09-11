package sn.sdley.springbootstarter0.service;

import sn.sdley.springbootstarter0.repositories.PaymentService;

public class StripePaymentService implements PaymentService {
    @Override
    public void processPayment(double amount) {
        System.out.println("Initiating payment processing through Stripe...");
        System.out.println("Processing payment of $" + amount + " through Stripe.");
        System.out.println("Payment processed successfully.");
    }
}
