package sn.sdley.springbootstarter0.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.dtos.CartDto;
import sn.sdley.springbootstarter0.dtos.CartItemDto;
import sn.sdley.springbootstarter0.entities.Cart;
import sn.sdley.springbootstarter0.exceptions.CartNotFoundException;
import sn.sdley.springbootstarter0.exceptions.ProductNotFoundException;
import sn.sdley.springbootstarter0.mappers.CartMapper;
import sn.sdley.springbootstarter0.repositories.CartRepository;
import sn.sdley.springbootstarter0.repositories.ProductRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var product = productRepository.findById(productId).orElse(null);
        if (product == null){
            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        return cartMapper.toDto(cart);
    }

    public CartItemDto updateItem(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var cartItem = cart.getItem(productId);
        if (cartItem == null) {
            throw new ProductNotFoundException();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public void removeItem(UUID cartId, Long productId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeItem(productId);
        cartRepository.save(cart);
    }

   public void clearCart(UUID cartId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.clear();
        cartRepository.save(cart);
    }
}
