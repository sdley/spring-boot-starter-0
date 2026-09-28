package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.sdley.springbootstarter0.entities.Order;
import sn.sdley.springbootstarter0.entities.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    @Query("SELECT o FROM Order o WHERE o.customer = :customer")
    List<Order> getAllByCustomer(@Param("customer") User customer);
}