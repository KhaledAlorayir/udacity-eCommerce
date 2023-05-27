package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getAllItemsTest() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getItemByIdHappyPath() {
        Item item = new Item();
        item.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(item.getId(), response.getBody().getId());
    }

    @Test
    public void getItemByInvalidId() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Item> response = itemController.getItemById(1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void getItemsByNameHappyPath() {
        Item item = new Item();
        item.setName("ps5");
        when(itemRepository.findByName("ps5")).thenReturn(List.of(item));
        ResponseEntity<List<Item>> response = itemController.getItemsByName("ps5");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1,response.getBody().size());
    }

    @Test
    public void getItemsByInvalidName() {
        when(itemRepository.findByName("ps5")).thenReturn(List.of());
        ResponseEntity<List<Item>> response = itemController.getItemsByName("ps5");
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
