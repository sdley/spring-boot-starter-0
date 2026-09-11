package sn.sdley.springbootstarter0.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.repositories.ProductRepository;
import sn.sdley.springbootstarter0.repositories.ProfileRepository;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class UserService {

    private final ProfileRepository profileRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void fetchProducts() {
        var products = productRepository.findProducts(BigDecimal.valueOf(1), BigDecimal.valueOf(5000));
        products.forEach(System.out::println);
    }

}
