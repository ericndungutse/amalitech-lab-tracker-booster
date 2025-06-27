package com.ndungutse.project_tracker.security;

import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // First try to find user by username
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElse(null);

        // If not found, try to find by email
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with username or email: " + usernameOrEmail));
        }

        return new CustomUserDetails(user);
    }
}