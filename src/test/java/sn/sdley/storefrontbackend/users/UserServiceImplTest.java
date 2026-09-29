package sn.sdley.storefrontbackend.users;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    void loadsUserDetailsByEmail() {
        var user = new User();
        user.setEmail("user@example.com");
        user.setPassword("stored-hash");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        var details = userService.loadUserByUsername("user@example.com");

        assertThat(details.getUsername()).isEqualTo("user@example.com");
        assertThat(details.getPassword()).isEqualTo("stored-hash");
        assertThat(details.getAuthorities()).isEmpty();
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void throwsUsernameNotFoundWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: missing@example.com");
    }
}
