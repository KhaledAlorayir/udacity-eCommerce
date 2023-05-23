package com.example.demo.security;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetails implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.model.persistence.User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("user not found"));
        return new User(String.valueOf(user.getId()),user.getPassword(), Collections.emptyList());
    }
}
