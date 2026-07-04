package com.hirehub.security;

import com.hirehub.exception.ResourceNotFoundException;
import com.hirehub.model.User;
import com.hirehub.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Small helper so services can easily get "who is making this request"
// without repeating SecurityContextHolder lookups everywhere
@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));
    }
}
