package com.example.teleport.service.impl;

import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import com.example.teleport.response.OptimizeResponse;
import com.example.teleport.service.ResponseBuilderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseBuilderServiceImpl implements ResponseBuilderService {

    @Override
    public OptimizeResponse buildEmptyResponse(TruckDto truck) {
        return OptimizeResponse.builder()
                .truckId(truck.getId())
                .selectedOrderIds(List.of())
                .totalPayoutCents(0L)
                .totalWeightLbs(0)
                .totalVolumeCuft(0)
                .utilizationWeightPercent(0.0)
                .utilizationVolumePercent(0.0)
                .build();
    }

    @Override
    public OptimizeResponse buildResponse(
            TruckDto truck,
            List<OrderDto> orders,
            int bestMask,
            long[] payout,
            int[] weight,
            int[] volume
    ) {
        List<String> selectedOrderIds = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            if ((bestMask & (1 << i)) != 0) {
                selectedOrderIds.add(orders.get(i).getId());
            }
        }

        double weightUtilization = (weight[bestMask] * 100.0) / truck.getMaxWeightLbs();
        double volumeUtilization = (volume[bestMask] * 100.0) / truck.getMaxVolumeCuft();

        return OptimizeResponse.builder()
                .truckId(truck.getId())
                .selectedOrderIds(selectedOrderIds)
                .totalPayoutCents(payout[bestMask])
                .totalWeightLbs(weight[bestMask])
                .totalVolumeCuft(volume[bestMask])
                .utilizationWeightPercent(Math.round(weightUtilization))
                .utilizationVolumePercent(Math.round(volumeUtilization))
                .build();
    }
}
