package com.example.teleport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OrderDto {

    @NotBlank(message = "order id can not be null or blank")
    private String id;
    @Min(1)
    private long payoutCents;
    @Min(1)
    private int weightLbs;
    @Min(1)
    private int volumeCuft;
    @NotBlank(message = "origin can not be null or blank")
    private String origin;
    @NotBlank(message = "destination can not be null or blank")
    private String destination;
    @NotNull(message = "pickup date can not be null")
    private LocalDate pickupDate;
    @NotNull(message = "delivery date can not be null")
    private LocalDate deliveryDate;
    @NotNull(message = "hazmat can not be null")
    private boolean isHazmat;

}
