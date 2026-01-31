package com.example.teleport.service;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.response.OptimizeResponse;

import java.util.List;

public interface ResponseBuilderService {
    OptimizeResponse buildEmptyResponse(TruckDto truck);
    OptimizeResponse buildResponse(
            TruckDto truck,
            List<OrderDto> orders,
            int bestMask,
            long[] payout,
            int[] weight,
            int[] volume
    );
}
