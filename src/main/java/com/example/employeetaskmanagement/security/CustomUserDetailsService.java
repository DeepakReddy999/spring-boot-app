package com.example.employeetaskmanagement.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.employeetaskmanagement.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = username == null ? null : username.trim().toLowerCase();
        com.example.employeetaskmanagement.entity.User user = userRepository.findByEmail(normalizedUsername)
            .orElseThrow(() -> {
                log.warn("action=load_user_failed email={} reason=user_not_found", normalizedUsername);
                return new UsernameNotFoundException("User not found with email: " + username);
            });

        if (!user.isActive()) {
            log.warn("action=load_user_failed email={} reason=user_inactive", normalizedUsername);
            throw new UsernameNotFoundException("User account is inactive.");
        }

        return new User(
            user.getEmail(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
