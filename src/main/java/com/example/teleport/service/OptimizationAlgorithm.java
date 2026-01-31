package com.example.teleport.service;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;

public interface OptimizationAlgorithm {
    OptimizationResult findOptimalCombination(TruckDto truck, java.util.List<OrderDto> orders);
    
    record OptimizationResult(int bestMask, long[] payout, int[] weight, int[] volume) {}
}
