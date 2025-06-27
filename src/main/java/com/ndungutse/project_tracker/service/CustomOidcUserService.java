package com.ndungutse.project_tracker.service;

import com.ndungutse.project_tracker.model.Role;
import com.ndungutse.project_tracker.model.User;
import com.ndungutse.project_tracker.repository.RoleRepository;
import com.ndungutse.project_tracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Call the default OidcUserService to load the user's information
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            // Get user information from claims
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName();
            // Check if user exists in database
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty()) {
                // Create new user with Contractor role
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(name);
                newUser.setPassword("Test@12345");
                newUser.setUsername(email);

                // Get Contractor role
                Role contractorRole = roleRepository.findByRoleName("CONTRACTOR")
                        .orElseThrow(() -> new RuntimeException("Contractor role not found"));

                newUser.setRole(contractorRole);
                userRepository.save(newUser);
            }

            return oidcUser;
        } catch (Exception ex) {
            // Handle any exceptions during custom processing
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }
}
