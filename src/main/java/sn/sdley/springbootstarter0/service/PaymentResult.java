package sn.sdley.springbootstarter0.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sn.sdley.springbootstarter0.entities.PaymentStatus;

@AllArgsConstructor
@Getter
public class PaymentResult {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
