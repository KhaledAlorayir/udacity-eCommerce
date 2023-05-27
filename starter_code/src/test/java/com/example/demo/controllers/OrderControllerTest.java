package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.dto.GetCartResponse;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private Authentication auth = mock(Authentication.class);
    private SecurityContext securityContext = mock(SecurityContext.class);
    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository",userRepository);
        TestUtils.injectObjects(orderController,"orderRepository",orderRepository);

        when(auth.getPrincipal()).thenReturn(1L);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void submitHappyPath() {
        User fakeUser = new User(1L, "khaled", "1111", new Cart());
        Item item = new Item(1L, "PS5", new BigDecimal(2500), "ps5");
        List<Item> items = new ArrayList<>();
        items.add(item);
        fakeUser.setCart(new Cart(1L, items,fakeUser,new BigDecimal(2500)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        ResponseEntity<GetCartResponse> response = orderController.submit();
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assert.assertEquals(item,response.getBody().getItems().get(0));
        Assert.assertEquals(new BigDecimal(2500),response.getBody().getTotal());
    }

    @Test
    public void submitHappyInvalidUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<GetCartResponse> response = orderController.submit();
        Assert.assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void getOrderForUserHappyPath() {
        User fakeUser = new User(1L, "khaled", "1111", new Cart());
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        ResponseEntity<List<GetCartResponse>> response = orderController.getOrdersForUser();
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }
    @Test
    public void getOrderForUserInvalidUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<List<GetCartResponse>> response = orderController.getOrdersForUser();
        Assert.assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

}
