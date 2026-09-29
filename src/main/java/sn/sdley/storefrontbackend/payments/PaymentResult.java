package sn.sdley.storefrontbackend.payments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sn.sdley.storefrontbackend.orders.PaymentStatus;

@AllArgsConstructor
@Getter
public class PaymentResult {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
