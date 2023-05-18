package com.example.demo.controllers;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.GetUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.dto.CreateUserRequest;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> findById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(new GetUserResponse(userRepository.findById(id).orElseThrow(() -> new NotFoundException("user doesn't exist"))));
    }

	@GetMapping("/username/{username}")
	public ResponseEntity<GetUserResponse> findByUserName(@Valid @PathVariable String username) {
		return ResponseEntity.ok(new GetUserResponse(userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user doesn't exist"))));
	}

    @PostMapping
    public ResponseEntity<GetUserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userRepository.findByUsername(createUserRequest.getUsername()).ifPresent(user -> {
            throw new BadRequestException("username exists");
        });

        if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmedPassword())) {
            throw new BadRequestException("passwords don't match");
        }
        return ResponseEntity.ok(new GetUserResponse(userRepository.save(new User(createUserRequest.getUsername(),passwordEncoder.encode(createUserRequest.getPassword()),new Cart()))));
    }


}
