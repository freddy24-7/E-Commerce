package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;

public class ItemControllerTest {

    private ItemController itemController;

    //Mocking itemRepository
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    //Mocks
    @Before
    public void setUp(){

        //Creating test case
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepository);
        Item item = new Item();
        item.setId(1L);
        item.setName("Top Item");
        item.setDescription("Outstanding in its use");
        item.setPrice(BigDecimal.valueOf(400));

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        //Scenarios, determined by controller end points
        when(itemRepository.findByName("Top Item")).thenReturn(itemList);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findAll()).thenReturn(itemList);

    }

    @Test
    public void get_items() {
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(1, responseItems.size());
        assertEquals("Top Item", responseItems.get(0).getName());
    }

    @Test
    public void get_item_by_id_success() {
        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item responseItem = response.getBody();
        assertNotNull(responseItem);
        assertEquals(1L, responseItem.getId().longValue());
        assertEquals("Top Item", responseItem.getName());
    }

    @Test
    public void get_item_by_id_not_found() {
        //Only one object defined, does item 2 should not exist
        ResponseEntity<Item> response = itemController.getItemById(2L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_items_by_name_success() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Top Item");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(1, responseItems.size());
        assertEquals("Top Item", responseItems.get(0).getName());
    }

    @Test
    public void get_items_by_name_not_found() {
        //An item by this name should not exist
        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistentItem");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
