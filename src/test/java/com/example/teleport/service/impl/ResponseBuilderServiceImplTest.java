package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.response.OptimizeResponse;
import com.example.teleport.service.ResponseBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseBuilderServiceImplTest {

    private ResponseBuilderService responseBuilderService;
    private TruckDto truck;
    private OrderDto order1;
    private OrderDto order2;

    @BeforeEach
    void setUp() {
        responseBuilderService = new ResponseBuilderServiceImpl();
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
    }

    @Test
    void testBuildEmptyResponse() {
        OptimizeResponse response = responseBuilderService.buildEmptyResponse(truck);

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        assertTrue(response.getSelectedOrderIds().isEmpty());
        assertEquals(0L, response.getTotalPayoutCents());
        assertEquals(0, response.getTotalWeightLbs());
        assertEquals(0, response.getTotalVolumeCuft());
        assertEquals(0.0, response.getUtilizationWeightPercent());
        assertEquals(0.0, response.getUtilizationVolumePercent());
    }

    @Test
    void testBuildResponse_SingleOrder() {
        List<OrderDto> orders = List.of(order1);
        int bestMask = 1;
        long[] payout = {0L, 10000L};
        int[] weight = {0, 2000};
        int[] volume = {0, 100};

        OptimizeResponse response = responseBuilderService.buildResponse(
                truck, orders, bestMask, payout, weight, volume
        );

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        assertEquals(1, response.getSelectedOrderIds().size());
        assertTrue(response.getSelectedOrderIds().contains("ORDER-001"));
        assertEquals(10000L, response.getTotalPayoutCents());
        assertEquals(2000, response.getTotalWeightLbs());
        assertEquals(100, response.getTotalVolumeCuft());
        assertEquals(20.0, response.getUtilizationWeightPercent());
        assertEquals(20.0, response.getUtilizationVolumePercent());
    }

    @Test
    void testBuildResponse_MultipleOrders() {
        List<OrderDto> orders = List.of(order1, order2);
        int bestMask = 3;
        long[] payout = {0L, 10000L, 15000L, 25000L};
        int[] weight = {0, 2000, 3000, 5000};
        int[] volume = {0, 100, 150, 250};

        OptimizeResponse response = responseBuilderService.buildResponse(
                truck, orders, bestMask, payout, weight, volume
        );

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        assertEquals(2, response.getSelectedOrderIds().size());
        assertTrue(response.getSelectedOrderIds().contains("ORDER-001"));
        assertTrue(response.getSelectedOrderIds().contains("ORDER-002"));
        assertEquals(25000L, response.getTotalPayoutCents());
        assertEquals(5000, response.getTotalWeightLbs());
        assertEquals(250, response.getTotalVolumeCuft());
        assertEquals(50.0, response.getUtilizationWeightPercent());
        assertEquals(50.0, response.getUtilizationVolumePercent());
    }

    @Test
    void testBuildResponse_NoOrdersSelected() {
        List<OrderDto> orders = List.of(order1);
        int bestMask = 0;
        long[] payout = {0L, 10000L};
        int[] weight = {0, 2000};
        int[] volume = {0, 100};

        OptimizeResponse response = responseBuilderService.buildResponse(
                truck, orders, bestMask, payout, weight, volume
        );

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        assertTrue(response.getSelectedOrderIds().isEmpty());
        assertEquals(0L, response.getTotalPayoutCents());
        assertEquals(0, response.getTotalWeightLbs());
        assertEquals(0, response.getTotalVolumeCuft());
        assertEquals(0.0, response.getUtilizationWeightPercent());
        assertEquals(0.0, response.getUtilizationVolumePercent());
    }

    @Test
    void testBuildResponse_UtilizationRounding() {
        TruckDto smallTruck = new TruckDto("TRUCK-002", 100, 50);
        List<OrderDto> orders = List.of(order1);
        int bestMask = 1;
        long[] payout = {0L, 10000L};
        int[] weight = {0, 33};
        int[] volume = {0, 17};

        OptimizeResponse response = responseBuilderService.buildResponse(
                smallTruck, orders, bestMask, payout, weight, volume
        );

        assertEquals(33.0, response.getUtilizationWeightPercent());
        assertEquals(34.0, response.getUtilizationVolumePercent());
    }
}
