package com.example.teleport.service;

import com.example.teleport.request.TruckLoadRequest;
import com.example.teleport.response.OptimizeResponse;

public interface OptimizerService {
    OptimizeResponse optimize(TruckLoadRequest truckLoadRequest);
}
