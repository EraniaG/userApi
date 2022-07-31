package com.nisum.userApi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserApiDetailsSecurityService extends UserDetailsService {
    Optional<UserDetails> loadUserByJwtToken(String jwtToken);
}
