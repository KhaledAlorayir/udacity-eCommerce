package com.example.demo.controllers;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.GetUserResponse;
import com.example.demo.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> findById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(new GetUserResponse(userRepository.findById(id).orElseThrow(() -> new NotFoundException("user doesn't exist"))));
    }

	@GetMapping("/username/{username}")
	public ResponseEntity<GetUserResponse> findByUserName(@Valid @PathVariable String username) {
		return ResponseEntity.ok(new GetUserResponse(userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("user doesn't exist"))));
	}

    @PostMapping
    public ResponseEntity<GetUserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userRepository.findByUsernameIgnoreCase(createUserRequest.getUsername()).ifPresent(user -> {
            log.error("create user error: username exists",createUserRequest.getUsername());
            throw new BadRequestException("username exists");
        });

        if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmedPassword())) {
            log.error("create user error: passwords don't match",createUserRequest.getPassword(),createUserRequest.getConfirmedPassword());
            throw new BadRequestException("passwords don't match");
        }
        GetUserResponse user = new GetUserResponse(userRepository.save(new User(createUserRequest.getUsername(),passwordEncoder.encode(createUserRequest.getPassword()),new Cart())));
        log.info("create user success",user.getId());
        return ResponseEntity.ok(user);
    }
}
