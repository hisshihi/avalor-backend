package com.hiss.avalor_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOptionDto {

    private String path;
    private double cost;
    private int estimatedDays;

}
