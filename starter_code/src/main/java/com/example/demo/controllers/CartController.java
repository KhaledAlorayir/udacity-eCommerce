package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.GetCartResponse;
import com.example.demo.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.dto.ModifyCartRequest;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<GetCartResponse> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findById(Authenticated.getUserId()).get();
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();

		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		return ResponseEntity.ok(new GetCartResponse(cart));
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<GetCartResponse> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findById(Authenticated.getUserId()).orElseThrow(() -> new NotFoundException("user doesn't exist"));;
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		return ResponseEntity.ok(new GetCartResponse(cart));
	}
		
}
