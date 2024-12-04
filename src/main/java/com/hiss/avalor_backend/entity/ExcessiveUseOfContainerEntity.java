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
@ToString
@Entity
public class ExcessiveUseOfContainerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String gettingStartedIssuanceFromSevenDays;
    String gettingStartedIssuanceFromEightFromNinetyNineDays;

    String gettingStartedUnloadingFromTenDays;
    String gettingStartedUnloadingFromElevenFromNinetyNineDays;

    String gettingStartedArrivalFromTenDays;
    String gettingStartedArrivalFromElevenFromNinetyNineDays;

}
