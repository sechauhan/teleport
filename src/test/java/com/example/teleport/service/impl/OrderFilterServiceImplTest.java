package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.service.OrderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderFilterServiceImplTest {

    private OrderFilterService orderFilterService;
    private OrderDto order1;
    private OrderDto order2;
    private OrderDto order3;

    @BeforeEach
    void setUp() {
        orderFilterService = new OrderFilterServiceImpl();
        
        order1 = new OrderDto(
                "ORDER-001",
                10000L,
                2000,
                100,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                false
        );
        
        order2 = new OrderDto(
                "ORDER-002",
                15000L,
                3000,
                150,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 6),
                false
        );
        
        order3 = new OrderDto(
                "ORDER-003",
                8000L,
                4000,
                200,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 3),
                LocalDate.of(2024, 1, 7),
                false
        );
    }

    @Test
    void testFilterCompatibleOrders_EmptyList() {
        List<OrderDto> orders = List.of();
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterCompatibleOrders_AllCompatible() {
        List<OrderDto> orders = List.of(order1, order2, order3);
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertEquals(3, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
        assertTrue(result.contains(order3));
    }

    @Test
    void testFilterCompatibleOrders_DifferentOrigin() {
        OrderDto differentOrigin = new OrderDto(
                "ORDER-004",
                5000L,
                1000,
                50,
                "Chicago",
                "Los Angeles",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                false
        );

        List<OrderDto> orders = List.of(order1, differentOrigin);
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertEquals(1, result.size());
        assertTrue(result.contains(order1));
        assertFalse(result.contains(differentOrigin));
    }

    @Test
    void testFilterCompatibleOrders_DifferentDestination() {
        OrderDto differentDestination = new OrderDto(
                "ORDER-004",
                5000L,
                1000,
                50,
                "New York",
                "Chicago",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                false
        );

        List<OrderDto> orders = List.of(order1, differentDestination);
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertEquals(1, result.size());
        assertTrue(result.contains(order1));
        assertFalse(result.contains(differentDestination));
    }

    @Test
    void testFilterCompatibleOrders_DifferentHazmat() {
        OrderDto hazmatOrder = new OrderDto(
                "ORDER-004",
                5000L,
                1000,
                50,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                true
        );

        List<OrderDto> orders = List.of(order1, hazmatOrder);
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertEquals(1, result.size());
        assertTrue(result.contains(order1));
        assertFalse(result.contains(hazmatOrder));
    }

    @Test
    void testFilterCompatibleOrders_InvalidDateRange() {
        OrderDto invalidDateOrder = new OrderDto(
                "ORDER-004",
                5000L,
                1000,
                50,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 5),
                LocalDate.of(2024, 1, 1),
                false
        );

        List<OrderDto> orders = List.of(order1, invalidDateOrder);
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertEquals(1, result.size());
        assertTrue(result.contains(order1));
        assertFalse(result.contains(invalidDateOrder));
    }

    @Test
    void testFilterCompatibleOrders_SameDateIsValid() {
        OrderDto sameDateOrder = new OrderDto(
                "ORDER-004",
                5000L,
                1000,
                50,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1),
                false
        );

        List<OrderDto> orders = List.of(order1, sameDateOrder);
        List<OrderDto> result = orderFilterService.filterCompatibleOrders(orders);

        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(sameDateOrder));
    }
}
