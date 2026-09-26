package sn.sdley.springbootstarter0.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import sn.sdley.springbootstarter0.dtos.ChangePasswordRequest;
import sn.sdley.springbootstarter0.dtos.RegisterUserRequest;
import sn.sdley.springbootstarter0.dtos.UpdateUserRequest;
import sn.sdley.springbootstarter0.dtos.UserDto;
import sn.sdley.springbootstarter0.mappers.UserMapper;
import sn.sdley.springbootstarter0.repositories.UserRepository;

import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing user accounts")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "List users", description = "Retrieves all users, sorted by ID, name, or email.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public Iterable<UserDto> getAllUsers(
            @Parameter(description = "Sort field: id, name, or email. Unsupported values default to id.")
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy
    ) {
        if (!Set.of("name", "email").contains(sortBy)) {
            sortBy = "id";
        }

        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                // .map(user -> userMapper.toDto(user))
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user", description = "Retrieves a user by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content)
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "The ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    @Operation(summary = "Register a user", description = "Creates a new user account. Email addresses must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Request validation failed", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email address is already registered", content = @Content)
    })
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration details for the new user", required = true)
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error", "Email already exists")
            );
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        var userDto = userMapper.toDto(user);
        var location = uriComponentsBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(location).body(userDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates the details of an existing user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content)
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "The ID of the user to update", required = true)
            @PathVariable(name = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user details", required = true)
            @RequestBody UpdateUserRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, user);
        user = userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Permanently deletes a user account by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "The ID of the user to delete", required = true)
            @PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    @Operation(summary = "Change a user's password", description = "Changes a user's password after validating the current password.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Current password is incorrect", content = @Content),
            @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content)
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "The ID of the user whose password will change", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Current and replacement passwords", required = true)
            @RequestBody ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();

    }

}
