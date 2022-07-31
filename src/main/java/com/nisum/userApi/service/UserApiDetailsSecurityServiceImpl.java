package com.nisum.userApi.service;

import com.nisum.userApi.model.entity.User;
import com.nisum.userApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserApiDetailsSecurityServiceImpl implements UserApiDetailsSecurityService {

    private final UserRepository userRepository;

    @Autowired
    public UserApiDetailsSecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("Username invalid"));
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getRoles())
                    .credentialsExpired(false)
                    .accountLocked(false)
                    .accountExpired(false)
                    .disabled(false)
                    .build();
    }

    @Override
    public Optional<UserDetails> loadUserByJwtToken(String jwtToken) {
        return Optional.empty();
    }
}
