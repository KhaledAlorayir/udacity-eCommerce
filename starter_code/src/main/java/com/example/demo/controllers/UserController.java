package com.example.demo.controllers;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.GetUserResponse;
import com.example.demo.security.Authenticated;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CartRepository cartRepository;

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
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        Cart cart = new Cart();
        user.setCart(cart);
        userRepository.save(user);
        cart.setUser(user);
        cartRepository.save(cart);

        log.info("create user success",user.getId());
        return new ResponseEntity<>(new GetUserResponse(user),HttpStatus.CREATED);
    }
}
