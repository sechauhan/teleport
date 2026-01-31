package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.request.TruckLoadRequest;
import com.example.teleport.response.OptimizeResponse;
import com.example.teleport.service.OptimizationAlgorithm;
import com.example.teleport.service.OptimizerService;
import com.example.teleport.service.OrderFilterService;
import com.example.teleport.service.ResponseBuilderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptimizerServiceImpl implements OptimizerService {

    private final OrderFilterService orderFilterService;
    private final OptimizationAlgorithm optimizationAlgorithm;
    private final ResponseBuilderService responseBuilderService;

    public OptimizerServiceImpl(
            OrderFilterService orderFilterService,
            OptimizationAlgorithm optimizationAlgorithm,
            ResponseBuilderService responseBuilderService
    ) {
        this.orderFilterService = orderFilterService;
        this.optimizationAlgorithm = optimizationAlgorithm;
        this.responseBuilderService = responseBuilderService;
    }

    @Override
    public OptimizeResponse optimize(TruckLoadRequest truckLoadRequest) {
        TruckDto truckDto = truckLoadRequest.getTruck();
        List<OrderDto> compatibleOrders = orderFilterService.filterCompatibleOrders(truckLoadRequest.getOrders());

        if (compatibleOrders.isEmpty()) {
            return responseBuilderService.buildEmptyResponse(truckDto);
        }

        OptimizationAlgorithm.OptimizationResult result = 
                optimizationAlgorithm.findOptimalCombination(truckDto, compatibleOrders);

        return responseBuilderService.buildResponse(
                truckDto,
                compatibleOrders,
                result.bestMask(),
                result.payout(),
                result.weight(),
                result.volume()
        );
    }
}
