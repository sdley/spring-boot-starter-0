package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.repository.CrudRepository;
import sn.sdley.springbootstarter0.entities.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // String
    List<Product> findByName(String name);
    List<Product> findByNameLike(String name);
    List<Product> findByNameNotLike(String name);
    List<Product> findByNameContaining(String name);
    List<Product> findByNameStartingWith(String name);
    List<Product> findByNameEndingWith(String name);
    List<Product> findByNameEndingWithIgnoreCase(String name);

    // Numbers
    List<Product> findByPrice(BigDecimal price);
    List<Product> findByPriceGreaterThan(BigDecimal price);
    List<Product> findByPriceGreaterThanEqual(BigDecimal price);
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByPriceBetween(BigDecimal startPrice, BigDecimal endPrice);

    // Null
    List<Product> findByDescriptionIsNull();
    List<Product> findByDescriptionNull();
    List<Product> findByDescriptionNotNull();

    // Multiple conditions
    List<Product> findByNameAndPrice(String name, BigDecimal price);
    List<Product> findByDescriptionNullAndNameNull();

    // Sorting (OrderBy)
    List<Product> findByNameOrderByPriceAsc(String name);

    // Limiting (Top, First)
    List<Product> findTop3ByName(String name);
    List<Product> findFirst3ByName(String name);
    List<Product> findTop3ByNameOrderByPriceDesc(String name);
    List<Product> findFirst5ByNameLikeOrderByPriceAsc(String name);



}
