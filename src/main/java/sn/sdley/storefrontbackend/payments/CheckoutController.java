package sn.sdley.storefrontbackend.payments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.sdley.storefrontbackend.common.ErrorDto;
import sn.sdley.storefrontbackend.carts.CartEmptyException;
import sn.sdley.storefrontbackend.carts.CartNotFoundException;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
@Tag(name = "Checkout", description = "Endpoints for creating payment sessions and receiving payment events")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    @Operation(
            summary = "Start checkout",
            description = "Creates an order from the specified non-empty cart and returns the hosted payment URL."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checkout session created"),
            @ApiResponse(responseCode = "400", description = "Cart does not exist, is empty, or request validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "500", description = "Payment provider could not create a session",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public CheckoutResponse checkout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The ID of the cart to check out.", required = true)
            @Valid @RequestBody CheckoutRequest request){
        return checkoutService.checkout(request);
    }

    @PostMapping("/webhook")
    @Operation(
            summary = "Receive a payment webhook",
            description = "Verifies and handles Stripe payment events. Unsupported event types are acknowledged without changing an order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook event handled"),
            @ApiResponse(responseCode = "500", description = "Webhook signature or event processing failed",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public void handleWebhook(
            @Parameter(
                    name = "Stripe-Signature",
                    in = ParameterIn.HEADER,
                    description = "Stripe signature used to verify the raw webhook payload.",
                    required = true
            )
            @RequestHeader Map<String, String> headers,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The unmodified Stripe webhook event payload.", required = true)
            @RequestBody String payload
    ){
        checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorDto> handlePaymentException(PaymentException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session: " + ex.getMessage()));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex){
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
