package sn.sdley.springbootstarter0.service;

public class OrderService {

    public void placeOrder() {
        System.out.println("Placing order...");
        var paymentService = new StripePaymentService();
        paymentService.processPayment(100.0);
    }
}
