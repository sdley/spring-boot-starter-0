package sn.sdley.springbootstarter0.payments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sn.sdley.springbootstarter0.orders.PaymentStatus;

@AllArgsConstructor
@Getter
public class PaymentResult {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
