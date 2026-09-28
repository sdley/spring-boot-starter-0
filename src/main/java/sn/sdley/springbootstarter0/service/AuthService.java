package sn.sdley.springbootstarter0.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.entities.User;
import sn.sdley.springbootstarter0.repositories.UserRepository;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    public User getCurrentUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }
}
