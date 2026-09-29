package sn.sdley.storefrontbackend.orders;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import sn.sdley.storefrontbackend.auth.AuthService;
import sn.sdley.storefrontbackend.users.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderMapper orderMapper = mock(OrderMapper.class);
    private final AuthService authService = mock(AuthService.class);
    private final OrderService orderService = new OrderService(orderRepository, orderMapper, authService);

    @Test
    void returnsOnlyCurrentUsersOrdersMappedToDtos() {
        var customer = new User();
        var firstOrder = orderPlacedBy(customer);
        var secondOrder = orderPlacedBy(customer);
        var firstDto = new OrderDto();
        var secondDto = new OrderDto();
        when(authService.getCurrentUser()).thenReturn(customer);
        when(orderRepository.getOrdersByCustomer(customer)).thenReturn(List.of(firstOrder, secondOrder));
        when(orderMapper.toDto(firstOrder)).thenReturn(firstDto);
        when(orderMapper.toDto(secondOrder)).thenReturn(secondDto);

        assertThat(orderService.getAllOrders()).containsExactly(firstDto, secondDto);

        verify(orderRepository).getOrdersByCustomer(customer);
        verify(orderMapper).toDto(firstOrder);
        verify(orderMapper).toDto(secondOrder);
    }

    @Test
    void returnsMappedOrderWhenCurrentUserOwnsIt() {
        var customer = new User();
        var order = orderPlacedBy(customer);
        var dto = new OrderDto();
        when(orderRepository.getOrderWithItems(42L)).thenReturn(Optional.of(order));
        when(authService.getCurrentUser()).thenReturn(customer);
        when(orderMapper.toDto(order)).thenReturn(dto);

        assertThat(orderService.getOrder(42L)).isSameAs(dto);

        verify(orderMapper).toDto(order);
    }

    @Test
    void deniesAccessWhenOrderBelongsToAnotherCustomer() {
        var orderOwner = new User();
        orderOwner.setId(1L);
        var currentUser = new User();
        currentUser.setId(2L);
        when(orderRepository.getOrderWithItems(42L)).thenReturn(Optional.of(orderPlacedBy(orderOwner)));
        when(authService.getCurrentUser()).thenReturn(currentUser);

        assertThatThrownBy(() -> orderService.getOrder(42L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied");

        verifyNoInteractions(orderMapper);
    }

    @Test
    void throwsWhenOrderDoesNotExist() {
        when(orderRepository.getOrderWithItems(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(42L))
                .isInstanceOf(OrderNotFoundException.class);

        verifyNoInteractions(authService, orderMapper);
    }

    private static Order orderPlacedBy(User customer) {
        var order = new Order();
        order.setCustomer(customer);
        return order;
    }
}
