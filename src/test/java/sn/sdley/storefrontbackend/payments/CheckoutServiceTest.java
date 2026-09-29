package sn.sdley.storefrontbackend.payments;

import org.junit.jupiter.api.Test;
import sn.sdley.storefrontbackend.auth.AuthService;
import sn.sdley.storefrontbackend.carts.Cart;
import sn.sdley.storefrontbackend.carts.CartEmptyException;
import sn.sdley.storefrontbackend.carts.CartItem;
import sn.sdley.storefrontbackend.carts.CartNotFoundException;
import sn.sdley.storefrontbackend.carts.CartRepository;
import sn.sdley.storefrontbackend.carts.CartService;
import sn.sdley.storefrontbackend.orders.Order;
import sn.sdley.storefrontbackend.orders.OrderRepository;
import sn.sdley.storefrontbackend.orders.PaymentStatus;
import sn.sdley.storefrontbackend.products.Product;
import sn.sdley.storefrontbackend.users.User;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final AuthService authService = mock(AuthService.class);
    private final CartService cartService = mock(CartService.class);
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final CheckoutService checkoutService = new CheckoutService(
            cartRepository, orderRepository, authService, cartService, paymentGateway);

    private final UUID cartId = UUID.randomUUID();

    @Test
    void rejectsMissingCart() {
        var request = request(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkoutService.checkout(request))
                .isInstanceOf(CartNotFoundException.class);

        verifyNoInteractions(authService, orderRepository, paymentGateway, cartService);
    }

    @Test
    void rejectsEmptyCart() {
        var request = request(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart(cartId)));

        assertThatThrownBy(() -> checkoutService.checkout(request))
                .isInstanceOf(CartEmptyException.class);

        verifyNoInteractions(authService, orderRepository, paymentGateway, cartService);
    }

    @Test
    void savesOrderCreatesPaymentSessionAndClearsCart() {
        var cart = cartWithProduct(cartId);
        var customer = new User();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(authService.getCurrentUser()).thenReturn(customer);
        doAnswer(invocation -> {
            ((Order) invocation.getArgument(0)).setId(91L);
            return invocation.getArgument(0);
        }).when(orderRepository).save(any(Order.class));
        when(paymentGateway.createCheckoutSession(any(Order.class)))
                .thenReturn(new CheckoutSession("https://payments.test/session"));

        var response = checkoutService.checkout(request(cartId));

        assertThat(response.getOrderId()).isEqualTo(91L);
        assertThat(response.getCheckoutUrl()).isEqualTo("https://payments.test/session");
        var orderCaptor = org.mockito.ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        var savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getCustomer()).isSameAs(customer);
        assertThat(savedOrder.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo("7.00");
        assertThat(savedOrder.getItems()).hasSize(1);
        verify(paymentGateway).createCheckoutSession(savedOrder);
        verify(cartService).clearCart(cartId);
    }

    @Test
    void deletesSavedOrderWhenPaymentSessionCreationFails() {
        var cart = cartWithProduct(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(authService.getCurrentUser()).thenReturn(new User());
        doAnswer(invocation -> {
            ((Order) invocation.getArgument(0)).setId(92L);
            return invocation.getArgument(0);
        }).when(orderRepository).save(any(Order.class));
        when(paymentGateway.createCheckoutSession(any(Order.class)))
                .thenThrow(new PaymentException("payment provider unavailable"));

        assertThatThrownBy(() -> checkoutService.checkout(request(cartId)))
                .isInstanceOf(PaymentException.class)
                .hasMessage("payment provider unavailable");

        var orderCaptor = org.mockito.ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        verify(orderRepository).delete(orderCaptor.getValue());
        verify(cartService, never()).clearCart(any());
    }

    @Test
    void appliesSupportedWebhookPaymentResult() {
        var request = webhookRequest();
        var order = new Order();
        when(paymentGateway.parseWebhookRequest(request))
                .thenReturn(Optional.of(new PaymentResult(93L, PaymentStatus.PAID)));
        when(orderRepository.findById(93L)).thenReturn(Optional.of(order));

        checkoutService.handleWebhookEvent(request);

        assertThat(order.getStatus()).isEqualTo(PaymentStatus.PAID);
        verify(orderRepository).save(order);
    }

    @Test
    void ignoresUnsupportedWebhookEvent() {
        var request = webhookRequest();
        when(paymentGateway.parseWebhookRequest(request)).thenReturn(Optional.empty());

        checkoutService.handleWebhookEvent(request);

        verifyNoInteractions(orderRepository);
    }

    private static CheckoutRequest request(UUID cartId) {
        var request = new CheckoutRequest();
        request.setCartId(cartId);
        return request;
    }

    private static WebhookRequest webhookRequest() {
        return new WebhookRequest(Map.of("signature", "test-signature"), "{}");
    }

    private static Cart cart(UUID id) {
        var cart = new Cart();
        cart.setId(id);
        return cart;
    }

    private static Cart cartWithProduct(UUID id) {
        var cart = cart(id);
        var product = Product.builder().id(17L).name("Coffee").price(new BigDecimal("3.50")).build();
        var item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        cart.getItems().add(item);
        return cart;
    }
}
