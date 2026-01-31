package com.example.teleport.controller;

import com.example.teleport.request.TruckLoadRequest;
import com.example.teleport.service.OptimizerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class TruckLoadOptimizerController {

    private final OptimizerService optimizerService;

    public TruckLoadOptimizerController(OptimizerService optimizerService) {
        this.optimizerService = optimizerService;
    }

    @PostMapping(path = "/optimize")
    public ResponseEntity<?> optimize(@Valid @RequestBody TruckLoadRequest truckLoadRequest) {
        return ResponseEntity.ok(optimizerService.optimize(truckLoadRequest));
    }
}
