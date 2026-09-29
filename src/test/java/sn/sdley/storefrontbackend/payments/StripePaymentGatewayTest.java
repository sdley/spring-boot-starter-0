package sn.sdley.storefrontbackend.payments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StripePaymentGatewayTest {

    private static final String WEBHOOK_SECRET = "whsec_test_secret";
    private StripePaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        paymentGateway = new StripePaymentGateway();
        ReflectionTestUtils.setField(paymentGateway, "webhookSecretKey", WEBHOOK_SECRET);
    }

    @Test
    void rejectsWebhookWithAnInvalidSignature() {
        var request = new WebhookRequest(
                Map.of("Stripe-Signature", "t=1,v1=invalid"),
                "{}"
        );

        assertThatThrownBy(() -> paymentGateway.parseWebhookRequest(request))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("Webhook signature verification failed");
    }

    @Test
    void acknowledgesSignedUnsupportedEventsWithoutProducingAPaymentResult() throws Exception {
        var timestamp = Instant.now().getEpochSecond();
        var payload = """
                {
                  "id": "evt_test",
                  "object": "event",
                  "api_version": "2025-01-27.acacia",
                  "created": %d,
                  "data": {"object": {"id": "cus_test", "object": "customer"}},
                  "livemode": false,
                  "pending_webhooks": 1,
                  "request": null,
                  "type": "customer.created"
                }
                """.formatted(timestamp);
        var signature = signature(timestamp, payload);
        var request = new WebhookRequest(
                Map.of("Stripe-Signature", "t=" + timestamp + ",v1=" + signature),
                payload
        );

        assertThat(paymentGateway.parseWebhookRequest(request)).isEmpty();
    }

    private static String signature(long timestamp, String payload) throws Exception {
        var mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        var signedPayload = timestamp + "." + payload;
        return HexFormat.of().formatHex(mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8)));
    }
}
