package com.example.teleport.util;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.response.OptimizeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptimizerUtilTest {

    private TruckDto truck;
    private OrderDto order1;
    private OrderDto order2;
    private OrderDto order3;

    @BeforeEach
    void setUp() {
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
    void testEmptyResponse() {
        OptimizeResponse response = OptimizerUtil.emptyResponse(truck);

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
        int bestMask = 1; // Select first order (bit 0 set)
        long[] payout = {0L, 10000L};
        int[] weight = {0, 2000};
        int[] volume = {0, 100};

        OptimizeResponse response = OptimizerUtil.buildResponse(
                truck, orders, bestMask, payout, weight, volume
        );

        assertNotNull(response);
        assertEquals("TRUCK-001", response.getTruckId());
        assertEquals(1, response.getSelectedOrderIds().size());
        assertTrue(response.getSelectedOrderIds().contains("ORDER-001"));
        assertEquals(10000L, response.getTotalPayoutCents());
        assertEquals(2000, response.getTotalWeightLbs());
        assertEquals(100, response.getTotalVolumeCuft());
        assertEquals(20.0, response.getUtilizationWeightPercent()); // 2000/10000 * 100 = 20
        assertEquals(20.0, response.getUtilizationVolumePercent()); // 100/500 * 100 = 20
    }

    @Test
    void testBuildResponse_MultipleOrders() {
        List<OrderDto> orders = List.of(order1, order2);
        int bestMask = 3; // Select both orders (bits 0 and 1 set: 0b11)
        long[] payout = {0L, 10000L, 15000L, 25000L};
        int[] weight = {0, 2000, 3000, 5000};
        int[] volume = {0, 100, 150, 250};

        OptimizeResponse response = OptimizerUtil.buildResponse(
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
        assertEquals(50.0, response.getUtilizationWeightPercent()); // 5000/10000 * 100 = 50
        assertEquals(50.0, response.getUtilizationVolumePercent()); // 250/500 * 100 = 50
    }

    @Test
    void testBuildResponse_NoOrdersSelected() {
        List<OrderDto> orders = List.of(order1);
        int bestMask = 0; // No orders selected
        long[] payout = {0L, 10000L};
        int[] weight = {0, 2000};
        int[] volume = {0, 100};

        OptimizeResponse response = OptimizerUtil.buildResponse(
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
        int[] weight = {0, 33}; // 33/100 = 33%
        int[] volume = {0, 17}; // 17/50 = 34%

        OptimizeResponse response = OptimizerUtil.buildResponse(
                smallTruck, orders, bestMask, payout, weight, volume
        );

        assertEquals(33.0, response.getUtilizationWeightPercent());
        assertEquals(34.0, response.getUtilizationVolumePercent());
    }

    @Test
    void testFilterCompatibleOrders_EmptyList() {
        List<OrderDto> orders = List.of();
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterCompatibleOrders_AllCompatible() {
        List<OrderDto> orders = List.of(order1, order2, order3);
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

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
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

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
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

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
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

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
                LocalDate.of(2024, 1, 1), // Delivery before pickup
                false
        );

        List<OrderDto> orders = List.of(order1, invalidDateOrder);
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

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
                LocalDate.of(2024, 1, 1), // Same date is valid
                false
        );

        List<OrderDto> orders = List.of(order1, sameDateOrder);
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(sameDateOrder));
    }

    @Test
    void testFilterCompatibleOrders_MixedCompatibility() {
        OrderDto compatibleOrder = new OrderDto(
                "ORDER-004",
                5000L,
                1000,
                50,
                "New York",
                "Los Angeles",
                LocalDate.of(2024, 1, 4),
                LocalDate.of(2024, 1, 8),
                false
        );

        OrderDto incompatibleOrder = new OrderDto(
                "ORDER-005",
                5000L,
                1000,
                50,
                "Chicago",
                "Los Angeles",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                false
        );

        List<OrderDto> orders = List.of(order1, compatibleOrder, incompatibleOrder);
        List<OrderDto> result = OptimizerUtil.filterCompatibleOrders(orders);

        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(compatibleOrder));
        assertFalse(result.contains(incompatibleOrder));
    }
}
