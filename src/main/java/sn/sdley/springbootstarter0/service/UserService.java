package sn.sdley.springbootstarter0.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.entities.Product;
import sn.sdley.springbootstarter0.repositories.ProductRepository;
import sn.sdley.springbootstarter0.repositories.ProfileRepository;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class UserService {

    private final ProfileRepository profileRepository;
    private final ProductRepository productRepository;

    public void fetchSortedProducts() {
        var sort = Sort.by(Sort.Order.asc("name"), Sort.Order.desc("price"));

        productRepository.findAll(sort).forEach(System.out::println);
    }

    public void fetchPaginatedProducts(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findAll(pageRequest);

        var products = productPage.getContent();
        products.forEach(System.out::println);

        var totalPages = productPage.getTotalPages();
        var totalElements = productPage.getTotalElements();

        System.out.println("Total Pages: " + totalPages);
        System.out.println("Total Elements: " + totalElements);

    }

    @Transactional
    public void fetchProducts() {
        var products = productRepository.findProducts(BigDecimal.valueOf(1), BigDecimal.valueOf(5000));
        products.forEach(System.out::println);
    }

}
