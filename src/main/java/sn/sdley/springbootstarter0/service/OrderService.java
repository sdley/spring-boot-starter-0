package sn.sdley.springbootstarter0.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.dtos.OrderDto;
import sn.sdley.springbootstarter0.mappers.OrderMapper;
import sn.sdley.springbootstarter0.repositories.OrderRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    private AuthService authService;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.getAllByCustomer(user);
        return orders.stream().map(orderMapper::toDto).toList();
    }
}
