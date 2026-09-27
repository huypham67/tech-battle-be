package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.enums.UserStatus;
import com.nhathuy.tech_battle_be.model.User;
import com.nhathuy.tech_battle_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(user.getStatus() == UserStatus.INACTIVE)
                .accountLocked(user.getStatus() == UserStatus.BLOCKED)
                .authorities("ROLE_" + user.getUserRole().name())
                .build();
    }
}
