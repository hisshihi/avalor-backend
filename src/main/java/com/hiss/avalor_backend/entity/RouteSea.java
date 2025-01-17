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
public class RouteSea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eqpt;
    private String containerTypeSize;
    private Integer filo;
    private Integer filoD;
    private Integer exclusive;
    private String cityFrom;
    private String cityTo;
    private String pol; // Port of loading or point of loading
    private String pod; // Port of discharge or point of discharge
    private String carrier;
    private String validTo;
    private String transportType;
}
