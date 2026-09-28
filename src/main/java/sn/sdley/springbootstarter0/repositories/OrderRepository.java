package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.sdley.springbootstarter0.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}