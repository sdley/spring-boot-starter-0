package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.sdley.springbootstarter0.entities.Cart;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}