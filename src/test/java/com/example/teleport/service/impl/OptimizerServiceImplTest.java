package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.request.TruckLoadRequest;
import com.example.teleport.response.OptimizeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptimizerServiceImplTest {

    private OptimizerServiceImpl optimizerService;
    private TruckDto truck;
    private OrderDto order1;
    private OrderDto order2;
    private OrderDto incompatibleOrder;
    private TruckLoadRequest request;

    @BeforeEach
    void setUp() {
        OrderFilterServiceImpl orderFilterService = new OrderFilterServiceImpl();
        KnapsackOptimizationAlgorithm optimizationAlgorithm = new KnapsackOptimizationAlgorithm();
        ResponseBuilderServiceImpl responseBuilderService = new ResponseBuilderServiceImpl();
        
        optimizerService = new OptimizerServiceImpl(
                orderFilterService,
                optimizationAlgorithm,
                responseBuilderService
        );
        
        truck = new TruckDto("TRUCK-001", 10000, 500);
        
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

        incompatibleOrder = new OrderDto(
                "ORDER-003",
                5000L,
                1000,
                50,
                "Chicago",
                "Los Angeles",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                false
        );

        request = new TruckLoadRequest(truck, List.of(order1, order2));
    }

    @Test
    void testOptimize_WithCompatibleOrders() {
        OptimizeResponse response = optimizerService.optimize(request);

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        assertFalse(response.getSelectedOrderIds().isEmpty());
        assertTrue(response.getTotalPayoutCents() > 0);
        assertTrue(response.getTotalWeightLbs() > 0);
        assertTrue(response.getTotalVolumeCuft() > 0);
    }

    @Test
    void testOptimize_NoCompatibleOrders() {
        TruckLoadRequest requestWithIncompatible = new TruckLoadRequest(
                truck, 
                List.of(order1, incompatibleOrder)
        );
        
        OptimizeResponse response = optimizerService.optimize(requestWithIncompatible);

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        // Should only include compatible orders (order1)
        assertTrue(response.getSelectedOrderIds().contains("ORDER-001"));
        assertFalse(response.getSelectedOrderIds().contains("ORDER-003"));
    }

    @Test
    void testOptimize_EmptyOrdersAfterFiltering() {
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

        TruckLoadRequest requestWithHazmat = new TruckLoadRequest(
                truck,
                List.of(order1, hazmatOrder)
        );

        OptimizeResponse response = optimizerService.optimize(requestWithHazmat);

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        // Should only include non-hazmat orders
        assertTrue(response.getSelectedOrderIds().contains("ORDER-001"));
        assertFalse(response.getSelectedOrderIds().contains("ORDER-004"));
    }
}
