package sn.sdley.storefrontbackend.products;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sn.sdley.storefrontbackend.auth.JwtAuthenticationFilter;
import sn.sdley.storefrontbackend.auth.JwtService;
import sn.sdley.storefrontbackend.auth.SecurityConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private ProductMapper productMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void filtersProductListByCategory() throws Exception {
        var product = Product.builder()
                .id(11L)
                .name("Desk lamp")
                .price(new BigDecimal("24.99"))
                .build();
        var dto = new ProductDto();
        dto.setId(11L);
        dto.setName("Desk lamp");
        when(productRepository.findByCategoryId((byte) 2)).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        mockMvc.perform(get("/products")
                        .param("categoryId", "2")
                        .with(user("customer").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].name").value("Desk lamp"));

        verify(productRepository).findByCategoryId((byte) 2);
    }

    @Test
    void createsProductInAnExistingCategory() throws Exception {
        var category = new Category("Office");
        category.setId((byte) 3);
        when(categoryRepository.findById((byte) 3)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(any(ProductDto.class)))
                .thenReturn(Product.builder().name("Notebook").build());
        doAnswer(invocation -> {
            ((Product) invocation.getArgument(0)).setId(23L);
            return invocation.getArgument(0);
        }).when(productRepository).save(any(Product.class));

        mockMvc.perform(post("/products")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Notebook",
                                  "description": "Lined notebook",
                                  "price": 4.50,
                                  "categoryId": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/products/23"))
                .andExpect(jsonPath("$.id").value(23));

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void rejectsProductCreationWhenCategoryDoesNotExist() throws Exception {
        when(categoryRepository.findById((byte) 99)).thenReturn(Optional.empty());

        mockMvc.perform(post("/products")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Notebook",
                                  "description": "Lined notebook",
                                  "price": 4.50,
                                  "categoryId": 99
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(productRepository, never()).save(any(Product.class));
    }
}
