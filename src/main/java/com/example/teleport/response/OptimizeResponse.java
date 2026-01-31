package com.example.teleport.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OptimizeResponse {

    private String truckId;
    private List<String> selectedOrderIds;
    private long totalPayoutCents;
    private int totalWeightLbs;
    private int totalVolumeCuft;
    private double utilizationWeightPercent;
    private double utilizationVolumePercent;

}
