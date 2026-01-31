package com.example.teleport.service;

import com.example.teleport.dto.OrderDto;

import java.util.List;

public interface OrderFilterService {
    List<OrderDto> filterCompatibleOrders(List<OrderDto> orders);
}
