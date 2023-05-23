package com.example.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.GetCartResponse;
import com.example.demo.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
	

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	
	
	@PostMapping("/submit")
	public ResponseEntity<GetCartResponse> submit() {
		User user = userRepository.findById(Authenticated.getUserId()).orElseThrow(() -> new NotFoundException("user doesn't exist"));
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		return ResponseEntity.ok(new GetCartResponse(order));
	}
	
	@GetMapping("/history")
	public ResponseEntity<List<GetCartResponse>> getOrdersForUser() {
		User user = userRepository.findById(Authenticated.getUserId()).orElseThrow(() -> new NotFoundException("user doesn't exist"));
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user).stream().map(order -> new GetCartResponse(order)).collect(Collectors.toList()));
	}
}
