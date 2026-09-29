package sn.sdley.storefrontbackend.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.DefaultApplicationArguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.sdley.storefrontbackend.products.CategoryRepository;
import sn.sdley.storefrontbackend.products.ProductRepository;
import sn.sdley.storefrontbackend.users.ProfileRepository;
import sn.sdley.storefrontbackend.users.UserRepository;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DevDataSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void seedsAllManagedTablesWhenEnabledAndDatabaseIsEmpty() {
        var devDataSeeder = new DevDataSeeder(new SeedProperties(true), userRepository,
                profileRepository, categoryRepository, productRepository);
        org.mockito.BDDMockito.given(userRepository.count()).willReturn(0L);
        org.mockito.BDDMockito.given(profileRepository.count()).willReturn(0L);
        org.mockito.BDDMockito.given(categoryRepository.count()).willReturn(0L);
        org.mockito.BDDMockito.given(productRepository.count()).willReturn(0L);

        devDataSeeder.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Iterable> categoriesCaptor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable> productsCaptor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable> usersCaptor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable> profilesCaptor = ArgumentCaptor.forClass(Iterable.class);

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        verify(productRepository).saveAll(productsCaptor.capture());
        verify(userRepository).saveAll(usersCaptor.capture());
        verify(profileRepository).saveAll(profilesCaptor.capture());

        assertThat(StreamSupport.stream(categoriesCaptor.getValue().spliterator(), false)).hasSize(10);
        assertThat(StreamSupport.stream(productsCaptor.getValue().spliterator(), false)).hasSize(15);
        assertThat(StreamSupport.stream(usersCaptor.getValue().spliterator(), false)).hasSize(12);
        assertThat(StreamSupport.stream(profilesCaptor.getValue().spliterator(), false)).hasSize(12);
    }

    @Test
    void skipsSeedingWhenAnyManagedTableAlreadyHasData() {
        var devDataSeeder = new DevDataSeeder(new SeedProperties(true), userRepository,
                profileRepository, categoryRepository, productRepository);
        org.mockito.BDDMockito.given(userRepository.count()).willReturn(1L);

        devDataSeeder.run(new DefaultApplicationArguments(new String[0]));

        verifyNoInteractions(profileRepository, categoryRepository, productRepository);
        verify(userRepository, never()).saveAll(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void skipsSeedingWhenDisabled() {
        var devDataSeeder = new DevDataSeeder(new SeedProperties(false), userRepository,
                profileRepository, categoryRepository, productRepository);

        devDataSeeder.run(new DefaultApplicationArguments(new String[0]));

        verifyNoInteractions(userRepository, profileRepository, categoryRepository, productRepository);
    }
}
