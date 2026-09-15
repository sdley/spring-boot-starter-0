package sn.sdley.springbootstarter0.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sn.sdley.springbootstarter0.entities.Address;
import sn.sdley.springbootstarter0.entities.Category;
import sn.sdley.springbootstarter0.entities.Product;
import sn.sdley.springbootstarter0.entities.Profile;
import sn.sdley.springbootstarter0.entities.User;
import sn.sdley.springbootstarter0.repositories.CategoryRepository;
import sn.sdley.springbootstarter0.repositories.ProductRepository;
import sn.sdley.springbootstarter0.repositories.ProfileRepository;
import sn.sdley.springbootstarter0.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final SeedProperties seedProperties;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedProperties.enabled()) {
            log.info("Application data seeding is disabled.");
            return;
        }

        if (hasExistingData()) {
            log.info("Application data seeding skipped because existing data was found.");
            return;
        }

        log.info("Application data seeding is enabled. Starting seed process.");

        Category electronics = new Category("Electronics");
        Category books = new Category("Books");
        Category home = new Category("Home");
        categoryRepository.saveAll(List.of(electronics, books, home));

        Product headphones = Product.builder()
                .name("Noise Cancelling Headphones")
                .description("Wireless headphones with active noise cancellation.")
                .price(new BigDecimal("199.99"))
                .category(electronics)
                .build();
        Product keyboard = Product.builder()
                .name("Mechanical Keyboard")
                .description("Compact keyboard with tactile switches.")
                .price(new BigDecimal("89.90"))
                .category(electronics)
                .build();
        Product springGuide = Product.builder()
                .name("Spring API Guide")
                .description("Hands-on guide for building Spring APIs.")
                .price(new BigDecimal("39.50"))
                .category(books)
                .build();
        Product deskLamp = Product.builder()
                .name("Desk Lamp")
                .description("LED desk lamp with adjustable brightness.")
                .price(new BigDecimal("24.99"))
                .category(home)
                .build();
        productRepository.saveAll(List.of(headphones, keyboard, springGuide, deskLamp));

        User alice = User.builder()
                .name("Alice Johnson")
                .email("alice.johnson@example.com")
                .password("dev-password-1")
                .build();
        alice.addAddress(Address.builder()
                .street("12 Market Street")
                .city("Dakar")
                .state("Dakar")
                .zip("11000")
                .build());
        alice.addAddress(Address.builder()
                .street("8 Coastal Road")
                .city("Mbour")
                .state("Thiès")
                .zip("23000")
                .build());
        alice.addFavoriteProduct(headphones);
        alice.addFavoriteProduct(springGuide);

        User bob = User.builder()
                .name("Bob Martin")
                .email("bob.martin@example.com")
                .password("dev-password-2")
                .build();
        bob.addAddress(Address.builder()
                .street("45 Independence Avenue")
                .city("Saint-Louis")
                .state("Saint-Louis")
                .zip("32000")
                .build());
        bob.addFavoriteProduct(keyboard);
        bob.addFavoriteProduct(deskLamp);

        User clara = User.builder()
                .name("Clara Ndiaye")
                .email("clara.ndiaye@example.com")
                .password("dev-password-3")
                .build();
        clara.addAddress(Address.builder()
                .street("77 River Lane")
                .city("Ziguinchor")
                .state("Ziguinchor")
                .zip("27000")
                .build());
        clara.addFavoriteProduct(springGuide);

        userRepository.saveAll(List.of(alice, bob, clara));

        profileRepository.saveAll(List.of(
                Profile.builder()
                        .user(alice)
                        .bio("Backend engineer testing the user API.")
                        .phoneNumber("+221770000001")
                        .dateOfBirth(LocalDate.of(1992, 4, 12))
                        .loyaltyPoints(180)
                        .build(),
                Profile.builder()
                        .user(bob)
                        .bio("QA analyst exercising catalog endpoints.")
                        .phoneNumber("+221770000002")
                        .dateOfBirth(LocalDate.of(1988, 9, 3))
                        .loyaltyPoints(95)
                        .build(),
                Profile.builder()
                        .user(clara)
                        .bio("Product manager validating demo scenarios.")
                        .phoneNumber("+221770000003")
                        .dateOfBirth(LocalDate.of(1995, 1, 25))
                        .loyaltyPoints(240)
                        .build()
        ));

        log.info("Application data seeding completed successfully.");
    }

    private boolean hasExistingData() {
        return userRepository.count() > 0
                || profileRepository.count() > 0
                || categoryRepository.count() > 0
                || productRepository.count() > 0;
    }
}
