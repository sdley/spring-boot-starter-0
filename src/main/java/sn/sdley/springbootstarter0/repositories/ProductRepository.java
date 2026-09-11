package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.sdley.springbootstarter0.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
