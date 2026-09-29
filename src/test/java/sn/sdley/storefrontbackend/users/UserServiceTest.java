package sn.sdley.storefrontbackend.users;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserService userService = new UserService(userRepository, userMapper, passwordEncoder);

    @Test
    void registersUserWithHashedPasswordAndDefaultRole() {
        var request = new RegisterUserRequest();
        request.setName("A User");
        request.setEmail("user@example.com");
        request.setPassword("plain-password");
        var user = new User();
        user.setPassword("plain-password");
        user.setRole(Role.ADMIN);
        var dto = new UserDto(7L, "A User", "user@example.com", null);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);
        when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");

        assertThat(userService.registerUser(request)).isSameAs(dto);

        assertThat(user.getPassword()).isEqualTo("hashed-password");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("plain-password");
    }

    @Test
    void rejectsDuplicateEmailWithoutCreatingOrSavingUser() {
        var request = new RegisterUserRequest();
        request.setEmail("user@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(DuplicateUserException.class);

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void updatesExistingUserAndReturnsMappedDto() {
        var user = new User();
        var request = new UpdateUserRequest();
        var dto = new UserDto(7L, "Updated", "updated@example.com", null);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        assertThat(userService.updateUser(7L, request)).isSameAs(dto);

        verify(userMapper).update(request, user);
        verify(userRepository).save(user);
    }

    @Test
    void updateAndDeleteRejectMissingUsers() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(7L, new UpdateUserRequest()))
                .isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.deleteUser(7L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(userRepository, never()).delete(org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void deletesExistingUser() {
        var user = new User();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        userService.deleteUser(7L);

        verify(userRepository).delete(user);
    }

    @Test
    void changesPasswordWhenOldPasswordMatches() {
        var user = new User();
        user.setPassword("existing-hash");
        var request = new ChangePasswordRequest();
        request.setOldPassword("old-password");
        request.setNewPassword("new-password");
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "existing-hash")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        userService.changePassword(7L, request);

        assertThat(user.getPassword()).isEqualTo("new-hash");
        verify(passwordEncoder).encode("new-password");
        verify(userRepository).save(user);
    }

    @Test
    void rejectsPasswordChangeWhenOldPasswordDoesNotMatch() {
        var user = new User();
        user.setPassword("existing-hash");
        var request = new ChangePasswordRequest();
        request.setOldPassword("wrong-password");
        request.setNewPassword("new-password");
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "existing-hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(7L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Password does not match");

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
        assertThat(user.getPassword()).isEqualTo("existing-hash");
    }

    @Test
    void passwordChangeRejectsMissingUser() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(7L, new ChangePasswordRequest()))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
