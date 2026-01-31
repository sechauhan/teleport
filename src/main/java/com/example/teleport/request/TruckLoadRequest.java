package com.example.teleport.request;

import com.example.teleport.config.OptimizerConstants;
import com.example.teleport.dto.OrderDto;
import com.example.teleport.dto.TruckDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TruckLoadRequest {
    @NotNull(message = "truck can not be null")
    @Valid
    private TruckDto truck;
    @NotNull(message = "orders can not be null")
    @NotEmpty(message = "orders can not be empty")
    @Size(max = OptimizerConstants.MAX_ORDERS_LIMIT, message = "Number of orders exceeds the maximum limit of " + OptimizerConstants.MAX_ORDERS_LIMIT)
    @Valid
    private List<OrderDto> orders = new ArrayList<>();
}
