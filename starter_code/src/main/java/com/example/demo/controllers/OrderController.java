package com.example.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.GetCartResponse;
import com.example.demo.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/orders")
public class OrderController {


    private  UserRepository userRepository;
    private  OrderRepository orderRepository;
    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/submit")
    public ResponseEntity<GetCartResponse> submit() {
        try {
            User user = userRepository.findById(Authenticated.getUserId()).orElseThrow(() -> new NotFoundException("user doesn't exist"));
            UserOrder order = UserOrder.createFromCart(user.getCart());
            orderRepository.save(order);
            log.info("submit order success", order.getId());
            return ResponseEntity.ok(new GetCartResponse(order));

        } catch (NotFoundException exception) {
            log.error("submit order error", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }

    @GetMapping("/history")
    public ResponseEntity<List<GetCartResponse>> getOrdersForUser() {
        try {
            User user = userRepository.findById(Authenticated.getUserId()).orElseThrow(() -> new NotFoundException("user doesn't exist"));
			log.info("order history success", user.getId());
			return ResponseEntity.ok(orderRepository.findByUser(user).stream().map(order -> new GetCartResponse(order)).collect(Collectors.toList()));
		} catch (NotFoundException exception) {
            log.error("order history error", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}
