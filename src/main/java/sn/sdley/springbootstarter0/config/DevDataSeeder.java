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
import java.util.Map;

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
        Category sports = new Category("Sports");
        Category beauty = new Category("Beauty");
        Category office = new Category("Office");
        Category groceries = new Category("Groceries");
        Category fashion = new Category("Fashion");
        Category toys = new Category("Toys");
        Category automotive = new Category("Automotive");

        categoryRepository.saveAll(List.of(
                electronics,
                books,
                home,
                sports,
                beauty,
                office,
                groceries,
                fashion,
                toys,
                automotive
        ));

        Product headphones = product("Noise Cancelling Headphones", "Wireless headphones with active noise cancellation.", "199.99", electronics);
        Product keyboard = product("Mechanical Keyboard", "Compact keyboard with tactile switches.", "89.90", electronics);
        Product monitor = product("4K Monitor", "27-inch display suited for development and design.", "329.00", electronics);
        Product springGuide = product("Spring API Guide", "Hands-on guide for building Spring APIs.", "39.50", books);
        Product cleanCode = product("Clean Code Handbook", "Reference book for maintainable software practices.", "31.25", books);
        Product deskLamp = product("Desk Lamp", "LED desk lamp with adjustable brightness.", "24.99", home);
        Product ergonomicChair = product("Ergonomic Chair", "Adjustable office chair for long work sessions.", "249.00", home);
        Product yogaMat = product("Yoga Mat", "Non-slip mat for stretching and workouts.", "29.90", sports);
        Product dumbbells = product("Adjustable Dumbbells", "Pair of adjustable dumbbells for home training.", "159.99", sports);
        Product faceSerum = product("Vitamin C Serum", "Daily skincare serum for brighter skin.", "18.75", beauty);
        Product notebookPack = product("Notebook Pack", "Set of premium ruled notebooks.", "14.50", office);
        Product coffeeBeans = product("Arabica Coffee Beans", "Medium roast whole coffee beans.", "16.20", groceries);
        Product jacket = product("Lightweight Jacket", "Water-resistant jacket for everyday wear.", "72.40", fashion);
        Product buildingBlocks = product("Building Blocks Set", "Creative block set for children.", "45.00", toys);
        Product carVacuum = product("Portable Car Vacuum", "Compact vacuum cleaner for car interiors.", "54.60", automotive);

        productRepository.saveAll(List.of(
                headphones,
                keyboard,
                monitor,
                springGuide,
                cleanCode,
                deskLamp,
                ergonomicChair,
                yogaMat,
                dumbbells,
                faceSerum,
                notebookPack,
                coffeeBeans,
                jacket,
                buildingBlocks,
                carVacuum
        ));

        User alice = user("Alice Johnson", "alice.johnson@example.com", "dev-password-1");
        addAddress(alice, "12 Market Street", "Dakar", "Dakar", "11000");
        addAddress(alice, "8 Coastal Road", "Mbour", "Thiès", "23000");
        alice.addFavoriteProduct(headphones);
        alice.addFavoriteProduct(springGuide);
        alice.addFavoriteProduct(faceSerum);

        User bob = user("Bob Martin", "bob.martin@example.com", "dev-password-2");
        addAddress(bob, "45 Independence Avenue", "Saint-Louis", "Saint-Louis", "32000");
        bob.addFavoriteProduct(keyboard);
        bob.addFavoriteProduct(deskLamp);

        User clara = user("Clara Ndiaye", "clara.ndiaye@example.com", "dev-password-3");
        addAddress(clara, "77 River Lane", "Ziguinchor", "Ziguinchor", "27000");
        clara.addFavoriteProduct(springGuide);
        clara.addFavoriteProduct(jacket);

        User david = user("David Fall", "david.fall@example.com", "dev-password-4");
        addAddress(david, "19 Plateau Residence", "Dakar", "Dakar", "11500");
        david.addFavoriteProduct(monitor);
        david.addFavoriteProduct(notebookPack);

        User emma = user("Emma Ba", "emma.ba@example.com", "dev-password-5");
        addAddress(emma, "5 Palm Grove", "Saly", "Thiès", "23120");
        emma.addFavoriteProduct(yogaMat);
        emma.addFavoriteProduct(faceSerum);

        User farid = user("Farid Sow", "farid.sow@example.com", "dev-password-6");
        addAddress(farid, "101 University District", "Dakar", "Dakar", "11850");
        addAddress(farid, "2 Corniche View", "Dakar", "Dakar", "11920");
        farid.addFavoriteProduct(cleanCode);
        farid.addFavoriteProduct(keyboard);

        User grace = user("Grace Diop", "grace.diop@example.com", "dev-password-7");
        addAddress(grace, "33 Garden Villas", "Kaolack", "Kaolack", "46000");
        grace.addFavoriteProduct(ergonomicChair);
        grace.addFavoriteProduct(coffeeBeans);

        User henry = user("Henry Sarr", "henry.sarr@example.com", "dev-password-8");
        addAddress(henry, "56 Trade Center", "Touba", "Diourbel", "25000");
        henry.addFavoriteProduct(carVacuum);
        henry.addFavoriteProduct(buildingBlocks);

        User ines = user("Ines Diallo", "ines.diallo@example.com", "dev-password-9");
        addAddress(ines, "88 Bayfront", "Mbour", "Thiès", "23210");
        ines.addFavoriteProduct(notebookPack);
        ines.addFavoriteProduct(jacket);

        User jules = user("Jules Camara", "jules.camara@example.com", "dev-password-10");
        addAddress(jules, "14 Arena Street", "Dakar", "Dakar", "11130");
        jules.addFavoriteProduct(dumbbells);
        jules.addFavoriteProduct(headphones);

        User khady = user("Khady Gueye", "khady.gueye@example.com", "dev-password-11");
        addAddress(khady, "9 Artisan Lane", "Thiès", "Thiès", "21000");
        khady.addFavoriteProduct(coffeeBeans);
        khady.addFavoriteProduct(cleanCode);

        User luc = user("Luc Faye", "luc.faye@example.com", "dev-password-12");
        addAddress(luc, "120 Innovation Hub", "Dakar", "Dakar", "11775");
        luc.addFavoriteProduct(monitor);
        luc.addFavoriteProduct(yogaMat);

        List<User> users = List.of(alice, bob, clara, david, emma, farid, grace, henry, ines, jules, khady, luc);
        userRepository.saveAll(users);

        profileRepository.saveAll(List.of(
                profile(alice, "Backend engineer testing the user API.", "+221770000001", LocalDate.of(1992, 4, 12), 180),
                profile(bob, "QA analyst exercising catalog endpoints.", "+221770000002", LocalDate.of(1988, 9, 3), 95),
                profile(clara, "Product manager validating demo scenarios.", "+221770000003", LocalDate.of(1995, 1, 25), 240),
                profile(david, "Technical writer documenting API examples.", "+221770000004", LocalDate.of(1991, 7, 19), 120),
                profile(emma, "Frontend developer testing product listings.", "+221770000005", LocalDate.of(1996, 11, 8), 310),
                profile(farid, "Software architect reviewing integration flows.", "+221770000006", LocalDate.of(1987, 6, 14), 425),
                profile(grace, "Operations lead preparing catalog demos.", "+221770000007", LocalDate.of(1990, 2, 27), 210),
                profile(henry, "Support engineer reproducing customer scenarios.", "+221770000008", LocalDate.of(1989, 12, 5), 160),
                profile(ines, "Business analyst validating reporting assumptions.", "+221770000009", LocalDate.of(1994, 3, 30), 275),
                profile(jules, "Fitness enthusiast testing wishlist combinations.", "+221770000010", LocalDate.of(1993, 5, 22), 140),
                profile(khady, "Local merchant simulating repeat customer orders.", "+221770000011", LocalDate.of(1997, 8, 17), 360),
                profile(luc, "Engineering manager reviewing seeded reference data.", "+221770000012", LocalDate.of(1986, 10, 11), 500)
        ));

        Map<String, Long> seededCounts = Map.of(
                "users", (long) users.size(),
                "profiles", 12L,
                "categories", 10L,
                "products", 15L
        );

        log.info("Application data seeding completed successfully: {}", seededCounts);
    }

    private boolean hasExistingData() {
        return userRepository.count() > 0
                || profileRepository.count() > 0
                || categoryRepository.count() > 0
                || productRepository.count() > 0;
    }

    private Product product(String name, String description, String price, Category category) {
        return Product.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .category(category)
                .build();
    }

    private User user(String name, String email, String password) {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
    }

    private void addAddress(User user, String street, String city, String state, String zip) {
        user.addAddress(Address.builder()
                .street(street)
                .city(city)
                .state(state)
                .zip(zip)
                .build());
    }

    private Profile profile(User user, String bio, String phoneNumber, LocalDate dateOfBirth, int loyaltyPoints) {
        return Profile.builder()
                .user(user)
                .bio(bio)
                .phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth)
                .loyaltyPoints(loyaltyPoints)
                .build();
    }
}
