package com.example.teleport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TruckDto {


    @NotBlank(message = "truck id can not be null or blank")
    private String id;
    @Min(1)
    private int maxWeightLbs;
    @Min(1)
    private int maxVolumeCuft;

}
