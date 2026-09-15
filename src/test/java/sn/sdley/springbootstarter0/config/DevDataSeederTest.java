package sn.sdley.springbootstarter0.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sn.sdley.springbootstarter0.repositories.CategoryRepository;
import sn.sdley.springbootstarter0.repositories.ProductRepository;
import sn.sdley.springbootstarter0.repositories.ProfileRepository;
import sn.sdley.springbootstarter0.repositories.UserRepository;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest(properties = "app.seed.enabled=true")
class DevDataSeederTest {

    @Autowired
    private DevDataSeeder devDataSeeder;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProfileRepository profileRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void seedsAllManagedTablesWhenEnabledAndDatabaseIsEmpty() throws Exception {
        org.mockito.BDDMockito.given(userRepository.count()).willReturn(0L);
        org.mockito.BDDMockito.given(profileRepository.count()).willReturn(0L);
        org.mockito.BDDMockito.given(categoryRepository.count()).willReturn(0L);
        org.mockito.BDDMockito.given(productRepository.count()).willReturn(0L);
        clearInvocations(categoryRepository, productRepository, userRepository, profileRepository);

        devDataSeeder.run(new DefaultApplicationArguments(new String[0]));

        org.mockito.ArgumentCaptor<Iterable> categoriesCaptor = org.mockito.ArgumentCaptor.forClass(Iterable.class);
        org.mockito.ArgumentCaptor<Iterable> productsCaptor = org.mockito.ArgumentCaptor.forClass(Iterable.class);
        org.mockito.ArgumentCaptor<Iterable> usersCaptor = org.mockito.ArgumentCaptor.forClass(Iterable.class);
        org.mockito.ArgumentCaptor<Iterable> profilesCaptor = org.mockito.ArgumentCaptor.forClass(Iterable.class);

        org.mockito.Mockito.verify(categoryRepository).saveAll(categoriesCaptor.capture());
        org.mockito.Mockito.verify(productRepository).saveAll(productsCaptor.capture());
        org.mockito.Mockito.verify(userRepository).saveAll(usersCaptor.capture());
        org.mockito.Mockito.verify(profileRepository).saveAll(profilesCaptor.capture());

        assertThat(StreamSupport.stream(categoriesCaptor.getValue().spliterator(), false)).hasSize(10);
        assertThat(StreamSupport.stream(productsCaptor.getValue().spliterator(), false)).hasSize(15);
        assertThat(StreamSupport.stream(usersCaptor.getValue().spliterator(), false)).hasSize(12);
        assertThat(StreamSupport.stream(profilesCaptor.getValue().spliterator(), false)).hasSize(12);
    }

    @Test
    void skipsSeedingWhenDataAlreadyExists() throws Exception {
        org.mockito.BDDMockito.given(userRepository.count()).willReturn(1L);
        clearInvocations(categoryRepository, productRepository, userRepository, profileRepository);

        devDataSeeder.run(new DefaultApplicationArguments(new String[0]));

        verifyNoInteractions(categoryRepository, productRepository, profileRepository);
    }
}
