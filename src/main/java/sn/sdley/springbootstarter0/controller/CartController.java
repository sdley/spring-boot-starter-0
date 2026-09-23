package sn.sdley.springbootstarter0.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import sn.sdley.springbootstarter0.dtos.AddItemToCartRequest;
import sn.sdley.springbootstarter0.dtos.CartDto;
import sn.sdley.springbootstarter0.dtos.CartItemDto;
import sn.sdley.springbootstarter0.dtos.UpdateCartItemRequest;
import sn.sdley.springbootstarter0.exceptions.CartNotFoundException;
import sn.sdley.springbootstarter0.exceptions.ProductNotFoundException;
import sn.sdley.springbootstarter0.service.CartService;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("carts")
@Tag(name = "Carts", description = "Endpoints for managing shopping carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Create a new cart", description = "Creates a new shopping cart and returns its details.")
    @ApiResponse(responseCode = "201", description = "Cart created successfully")
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriComponentsBuilder
    ) {
        var cartDto = cartService.createCart();
        var uri = uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add an item to the cart", description = "Adds a product to the specified shopping cart and returns the updated cart item details.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item added to the cart successfully"),
            @ApiResponse(responseCode = "400", description = "Product does not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart does not exist", content = @Content)
    })
    public ResponseEntity<CartItemDto> addItemToCart(
            @Parameter(description = "The ID of the cart to which the item will be added", required = true)
            @PathVariable UUID cartId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The product to add to the cart", required = true)
            @RequestBody AddItemToCartRequest request
    ){
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());


        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get a cart", description = "Retrieves the details of the specified shopping cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Cart does not exist", content = @Content)
    })
    public CartDto getCart(
            @Parameter(description = "The ID of the cart to retrieve", required = true)
            @PathVariable UUID cartId) {
        return cartService.getCart(cartId);
    }

    @PutMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Update a cart item", description = "Updates the quantity of a specific product in the specified shopping cart and returns the updated cart item details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Product is not in the cart", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart does not exist", content = @Content)
    })
    public CartItemDto updateCartItem(
            @Parameter(description = "The ID of the cart containing the item", required = true)
            @PathVariable("cartId") UUID cartId,
            @Parameter(description = "The ID of the product to update", required = true)
            @PathVariable("productId") Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The new quantity for the cart item", required = true)
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(cartId, productId, request.getQuantity());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Remove a cart item", description = "Removes a specific product from the specified shopping cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Cart does not exist", content = @Content)
    })
    public ResponseEntity<?> removeItem(
            @Parameter(description = "The ID of the cart containing the item", required = true)
            @PathVariable("cartId") UUID cartId,
            @Parameter(description = "The ID of the product to remove", required = true)
            @PathVariable("productId") Long productId
    ) {
        cartService.removeItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    @Operation(summary = "Clear a cart", description = "Removes all items from the specified shopping cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Cart does not exist", content = @Content)
    })
    public ResponseEntity<?> clearCart(
            @Parameter(description = "The ID of the cart to clear", required = true)
            @PathVariable("cartId") UUID cartId) {
        cartService.clearCart(cartId);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart not found")
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFoundException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "Product not found")
        );
    }
}
