package sn.sdley.storefrontbackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import sn.sdley.storefrontbackend.carts.Cart;
import sn.sdley.storefrontbackend.orders.Order;
import sn.sdley.storefrontbackend.orders.OrderRepository;
import sn.sdley.storefrontbackend.orders.PaymentStatus;
import sn.sdley.storefrontbackend.products.Category;
import sn.sdley.storefrontbackend.products.CategoryRepository;
import sn.sdley.storefrontbackend.products.Product;
import sn.sdley.storefrontbackend.products.ProductRepository;
import sn.sdley.storefrontbackend.users.Role;
import sn.sdley.storefrontbackend.users.User;
import sn.sdley.storefrontbackend.users.UserRepository;
import sn.sdley.storefrontbackend.support.MySqlTestConfiguration;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"app.seed.enabled=false", "spring.jpa.hibernate.ddl-auto=validate"})
@AutoConfigureMockMvc
@Import(MySqlTestConfiguration.class)
class StorefrontPersistenceIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void appliesAllFlywayMigrationsAgainstMySql() {
        var successfulMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1", Integer.class);

        assertThat(successfulMigrations).isEqualTo(4);
    }

    @Test
    @Transactional
    void persistsCatalogAndOrderWithTheExpectedRelationshipsAndTotals() {
        var category = categoryRepository.save(new Category("Integration category"));
        var product = productRepository.save(Product.builder()
                .name("Integration product")
                .description("A product used by database integration tests")
                .price(new BigDecimal("12.50"))
                .category(category)
                .build());

        assertThat(productRepository.findByCategoryId(category.getId()))
                .extracting(Product::getId)
                .containsExactly(product.getId());

        var customer = userRepository.save(User.builder()
                .name("Integration customer")
                .email("persistence@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build());

        var cart = new Cart();
        cart.addItem(product);
        var order = orderRepository.save(Order.fromCart(cart, customer));

        var persistedOrder = orderRepository.getOrderWithItems(order.getId()).orElseThrow();
        assertThat(persistedOrder.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(persistedOrder.getTotalPrice()).isEqualByComparingTo("12.50");
        assertThat(persistedOrder.getItems())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.getQuantity()).isEqualTo(1);
                    assertThat(item.getUnitPrice()).isEqualByComparingTo("12.50");
                    assertThat(item.getTotalPrice()).isEqualByComparingTo("12.50");
                });
        assertThat(orderRepository.getOrdersByCustomer(customer)).extracting(Order::getId)
                .containsExactly(order.getId());
    }
}
