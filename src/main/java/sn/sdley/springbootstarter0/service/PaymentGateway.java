package sn.sdley.springbootstarter0.service;

import sn.sdley.springbootstarter0.entities.Order;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(Order order);
}
