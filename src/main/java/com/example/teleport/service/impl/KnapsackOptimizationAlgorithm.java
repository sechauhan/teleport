package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.service.OptimizationAlgorithm;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KnapsackOptimizationAlgorithm implements OptimizationAlgorithm {

    @Override
    public OptimizationResult findOptimalCombination(TruckDto truck, List<OrderDto> orders) {
        int n = orders.size();
        int maxMask = 1 << n;
        
        long[] payout = new long[maxMask];
        int[] weight = new int[maxMask];
        int[] volume = new int[maxMask];
        boolean[] valid = new boolean[maxMask];

        valid[0] = true;

        long bestPayout = 0;
        int bestMask = 0;

        for (int i = 1; i < maxMask; i++) {
            int lsb = Integer.numberOfTrailingZeros(i);
            int prev = i & (i - 1);

            if (!valid[prev]) {
                continue;
            }

            OrderDto orderDto = orders.get(lsb);
            weight[i] = weight[prev] + orderDto.getWeightLbs();
            volume[i] = volume[prev] + orderDto.getVolumeCuft();

            if (weight[i] > truck.getMaxWeightLbs() || volume[i] > truck.getMaxVolumeCuft()) {
                continue;
            }

            payout[i] = payout[prev] + orderDto.getPayoutCents();
            valid[i] = true;

            if (payout[i] > bestPayout) {
                bestPayout = payout[i];
                bestMask = i;
            }
        }

        return new OptimizationResult(bestMask, payout, weight, volume);
    }
}
