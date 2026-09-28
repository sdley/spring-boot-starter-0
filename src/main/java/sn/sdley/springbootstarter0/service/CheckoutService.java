package sn.sdley.springbootstarter0.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.dtos.CheckoutRequest;
import sn.sdley.springbootstarter0.dtos.CheckoutResponse;
import sn.sdley.springbootstarter0.dtos.ErrorDto;
import sn.sdley.springbootstarter0.entities.Order;
import sn.sdley.springbootstarter0.exceptions.CartEmptyException;
import sn.sdley.springbootstarter0.exceptions.CartNotFoundException;
import sn.sdley.springbootstarter0.repositories.CartRepository;
import sn.sdley.springbootstarter0.repositories.OrderRepository;

@AllArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;

    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.findById(request.getCartId()).orElse(null);
        if(cart == null) {
            throw new CartNotFoundException();
        }

        if (cart.isEmpty()){
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);

        cartService.clearCart(cart.getId());

        return new CheckoutResponse(order.getId());
    }
}
