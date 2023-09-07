package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class OrderControllerTest {

    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_order_success() {
        User user = createUser();
        Cart cart = createCart();
        user.setCart(cart);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        UserOrder expectedOrder = createUserOrder(user, cart);
        expectedOrder.setUser(user);

        when(orderRepository.save(org.mockito.ArgumentMatchers.any(UserOrder.class))).thenReturn(expectedOrder);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertNotNull(response);
        assertEquals(OK, response.getStatusCode());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void submit_order_user_not_found() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void get_orders_for_user_success() {
        User user = createUser();
        Cart cart = createCart();
        UserOrder order = createUserOrder(user, cart);
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertNotNull(response);
        assertEquals(OK, response.getStatusCode());

        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());
        assertEquals(user, userOrders.get(0).getUser());
    }


    @Test
    public void get_orders_for_user_user_not_found() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertNotNull(response);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    //Helper methods to create test objects
    private User createUser() {
        User user = new User();
        user.setUsername("testUser");
        return user;
    }

    private Cart createCart() {
        Cart cart = new Cart();
        Item item = createItem();
        cart.addItem(item);
        return cart;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Top Item");
        item.setDescription("Outstanding in its use");
        item.setPrice(BigDecimal.valueOf(400));
        return item;
    }

    private UserOrder createUserOrder(User user, Cart cart) {
        UserOrder userOrder = UserOrder.createFromCart(cart);
        userOrder.setId(1L);
        userOrder.setUser(user);
        return userOrder;
    }

}
