package sn.sdley.storefrontbackend.orders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import sn.sdley.storefrontbackend.common.ErrorDto;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Endpoints for viewing the authenticated customer's orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "List my orders", description = "Retrieves all orders placed by the authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content)
    })
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get an order", description = "Retrieves an order placed by the authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "403", description = "The order belongs to another customer", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order does not exist", content = @Content)
    })
    public OrderDto getOrder(
            @Parameter(description = "The ID of the order to retrieve", required = true)
            @PathVariable("orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleOrderNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto(ex.getMessage()));
    }
}
