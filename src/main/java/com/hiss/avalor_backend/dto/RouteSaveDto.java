package com.hiss.avalor_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteSaveDto {

    private String cityFrom;
    private String cityTo;
    private String pol; // Port of loading or point of loading
    private String pod; // Port of discharge or point of discharge
    private String carrier;
    private String validTo;
    private String transportType;
    private String containerTypeSize;

    //    sea
    private String eqpt;
    private Integer filo;
    private Integer exclusive;

    //    railway
    private Integer filo20;
    private Integer filo20HC;
    private Integer filo40;

}
