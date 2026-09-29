package sn.sdley.storefrontbackend.carts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sn.sdley.storefrontbackend.products.Product;
import sn.sdley.storefrontbackend.products.ProductNotFoundException;
import sn.sdley.storefrontbackend.products.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final CartMapper cartMapper = mock(CartMapper.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CartService cartService = new CartService(cartRepository, cartMapper, productRepository);

    private final UUID cartId = UUID.randomUUID();
    private final Product product = product(17L);

    @BeforeEach
    void setUp() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart(cartId)));
    }

    @Test
    void createsAndMapsCart() {
        var dto = new CartDto();
        when(cartMapper.toDto(any(Cart.class))).thenReturn(dto);

        assertThat(cartService.createCart()).isSameAs(dto);

        verify(cartRepository).save(any(Cart.class));
        verify(cartMapper).toDto(any(Cart.class));
    }

    @Test
    void addsProductAndMapsNewCartItem() {
        var cart = cart(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        var itemDto = new CartItemDto();
        when(cartMapper.toDto(any(CartItem.class))).thenReturn(itemDto);

        assertThat(cartService.addToCart(cartId, product.getId())).isSameAs(itemDto);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItem(product.getId()).getQuantity()).isEqualTo(1);
        verify(cartRepository).save(cart);
    }

    @Test
    void addingExistingProductIncrementsQuantity() {
        var cart = cart(cartId);
        var existing = addProduct(cart, product, 2);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        cartService.addToCart(cartId, product.getId());

        assertThat(cart.getItems()).containsExactly(existing);
        assertThat(existing.getQuantity()).isEqualTo(3);
        verify(cartRepository).save(cart);
    }

    @Test
    void updatesItemQuantity() {
        var cart = cart(cartId);
        var item = addProduct(cart, product, 1);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        var itemDto = new CartItemDto();
        when(cartMapper.toDto(item)).thenReturn(itemDto);

        assertThat(cartService.updateItem(cartId, product.getId(), 4)).isSameAs(itemDto);

        assertThat(item.getQuantity()).isEqualTo(4);
        verify(cartRepository).save(cart);
    }

    @Test
    void removesItemAndSavesCart() {
        var cart = cart(cartId);
        var item = addProduct(cart, product, 1);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.removeItem(cartId, product.getId());

        assertThat(cart.getItems()).isEmpty();
        assertThat(item.getCart()).isNull();
        verify(cartRepository).save(cart);
    }

    @Test
    void clearsAllItemsAndSavesCart() {
        var cart = cart(cartId);
        addProduct(cart, product, 2);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.clearCart(cartId);

        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void rejectsMissingCart() {
        var missingId = UUID.randomUUID();
        when(cartRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart(missingId))
                .isInstanceOf(CartNotFoundException.class);
        assertThatThrownBy(() -> cartService.addToCart(missingId, product.getId()))
                .isInstanceOf(CartNotFoundException.class);
        assertThatThrownBy(() -> cartService.updateItem(missingId, product.getId(), 2))
                .isInstanceOf(CartNotFoundException.class);
        assertThatThrownBy(() -> cartService.removeItem(missingId, product.getId()))
                .isInstanceOf(CartNotFoundException.class);
        assertThatThrownBy(() -> cartService.clearCart(missingId))
                .isInstanceOf(CartNotFoundException.class);

        verifyNoInteractions(productRepository);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void rejectsMissingProductWhenAdding() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(cartId, product.getId()))
                .isInstanceOf(ProductNotFoundException.class);

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void rejectsProductNotPresentInCartWhenUpdating() {
        var cart = cart(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.updateItem(cartId, product.getId(), 2))
                .isInstanceOf(ProductNotFoundException.class);

        verify(cartRepository, never()).save(any(Cart.class));
    }

    private static Cart cart(UUID id) {
        var cart = new Cart();
        cart.setId(id);
        return cart;
    }

    private static CartItem addProduct(Cart cart, Product product, int quantity) {
        var item = cart.addItem(product);
        item.setQuantity(quantity);
        return item;
    }

    private static Product product(Long id) {
        return Product.builder().id(id).name("Coffee").price(new BigDecimal("3.50")).build();
    }
}
