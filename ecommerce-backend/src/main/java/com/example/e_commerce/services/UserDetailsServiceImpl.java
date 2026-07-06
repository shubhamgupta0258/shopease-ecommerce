package com.example.e_commerce.services;

import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

    @Service
    public class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

            // Defensive fallback for any legacy row without a role set
            String role = user.getRole() != null ? user.getRole() : "ROLE_USER";

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }
    }
//    Spring Security calls loadUserByUsername(email) to fetch user credentials during login.
//    You fetch user data from your database via repository, then wrap it into Spring Security’s UserDetails.
//    This allows your AuthenticationManager to verify credentials with the user’s encoded password.
