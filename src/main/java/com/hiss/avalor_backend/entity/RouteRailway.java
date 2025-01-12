package com.hiss.avalor_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@ToString
public class RouteRailway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer filo20;
    private Integer filo20HC;
    private Integer filo40;
    private String containerTypeSize = "COC"; // Default value
    private String cityFrom;
    private String cityTo;
    private String pol; // Port of loading or point of loading
    private String pod; // Port of discharge or point of discharge
    private String carrier;
    private String validTo;
    private String transportType;
    private Integer exclusive;
}
