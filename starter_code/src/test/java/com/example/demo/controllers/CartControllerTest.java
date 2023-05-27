package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.dto.GetCartResponse;
import com.example.demo.model.dto.ModifyCartRequest;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.Authenticated;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private Authentication auth = mock(Authentication.class);
    private SecurityContext securityContext = mock(SecurityContext.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        when(auth.getPrincipal()).thenReturn(1L);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void addToCartHappyPath() {
        User fakeUser = new User(1L, "khaled", "1111", new Cart());
        Item item = new Item(1L, "PS5", new BigDecimal(2500), "ps5");
        fakeUser.setCart(new Cart(1L, new ArrayList<>(),fakeUser,new BigDecimal(2500)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));


        ModifyCartRequest request = new ModifyCartRequest(1L, 1);
        ResponseEntity<GetCartResponse> response = cartController.addTocart(request);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(item,response.getBody().getItems().get(0));
        Assert.assertEquals(fakeUser.getUsername(),response.getBody().getUser().getUsername());
    }

    @Test
    public void addToCartInvalidItem() {
        User fakeUser = new User(1L, "khaled", "1111", new Cart());
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest(1L, 1);
        ResponseEntity<GetCartResponse> response = cartController.addTocart(request);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void removeFromCartHappyPath() {
        User fakeUser = new User(1L, "khaled", "1111", new Cart());
        Item item = new Item(1L, "PS5", new BigDecimal(2500), "ps5");
        List<Item> items = new ArrayList<>();
        items.add(item);
        fakeUser.setCart(new Cart(1L, items,fakeUser,new BigDecimal(2500)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest(1L, 1);
        ResponseEntity<GetCartResponse> response = cartController.removeFromcart(request);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(0,response.getBody().getItems().size());
    }

    @Test
    public void removeFromCartInvalidItem() {
        User fakeUser = new User(1L, "khaled", "1111", new Cart());
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest(1L, 1);
        ResponseEntity<GetCartResponse> response = cartController.removeFromcart(request);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
