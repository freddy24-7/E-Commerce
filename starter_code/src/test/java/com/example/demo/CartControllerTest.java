package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class CartControllerTest {

    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_success() {
        User user = createUser();
        Item item = createItem();

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(createCartRequest());

        assertNotNull(response);
        assertEquals(OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals("Top Item", cart.getItems().get(0).getName());
    }

    @Test
    public void add_to_cart_user_not_found() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(createCartRequest());

        assertNotNull(response);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = createUser();

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(createCartRequest());

        assertNotNull(response);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void remove_from_cart_success() {
        User user = createUser();
        Item item = createItem();
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        cart.addItem(item);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(createCartRequest());

        assertNotNull(response);
        assertEquals(OK, response.getStatusCode());

        Cart updatedCart = response.getBody();
        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getItems().size());
    }


    //Helper methods to create test objects
    private User createUser() {
        User user = new User();
        user.setUsername("testUser");
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Top Item");
        item.setDescription("Outstanding in its use");
        item.setPrice(BigDecimal.valueOf(400));
        return item;
    }

    private ModifyCartRequest createCartRequest() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        return request;
    }
}
