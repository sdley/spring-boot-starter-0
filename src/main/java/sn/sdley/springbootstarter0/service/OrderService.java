package sn.sdley.springbootstarter0.service;

public class OrderService {
    private PaymentService paymentService;

//    public OrderService(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }

    public void placeOrder() {
        System.out.println("Placing order...");
        paymentService.processPayment(100.0);
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
