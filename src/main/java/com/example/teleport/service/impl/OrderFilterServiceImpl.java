package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.service.OrderFilterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderFilterServiceImpl implements OrderFilterService {

    @Override
    public List<OrderDto> filterCompatibleOrders(List<OrderDto> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        OrderDto first = orders.getFirst();
        String origin = first.getOrigin();
        String destination = first.getDestination();
        boolean hazmat = first.isHazmat();

        return orders.stream()
                .filter(o -> o.getOrigin().equals(origin))
                .filter(o -> o.getDestination().equals(destination))
                .filter(o -> o.isHazmat() == hazmat)
                .filter(o -> !o.getPickupDate().isAfter(o.getDeliveryDate()))
                .toList();
    }
}
