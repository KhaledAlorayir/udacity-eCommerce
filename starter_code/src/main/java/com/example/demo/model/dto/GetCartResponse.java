package com.example.demo.model.dto;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCartResponse {
    private Long id;
    private List<Item> items;
    private GetUserResponse user;
    private BigDecimal total;

    public GetCartResponse(Cart cart) {
        this(cart.getId(), cart.getItems(), new GetUserResponse(cart.getUser()), cart.getTotal());
    }

    public GetCartResponse(UserOrder order) {
        this(order.getId(), order.getItems(), new GetUserResponse(order.getUser()), order.getTotal());
    }}
