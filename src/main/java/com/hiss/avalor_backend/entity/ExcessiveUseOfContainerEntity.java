package com.hiss.avalor_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

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
    Double gettingStartedIssuanceFromSevenDaysPrice;
    String gettingStartedIssuanceFromEightFromNinetyNineDays;
    Double gettingStartedIssuanceFromEightFromNinetyNineDaysPrice;

    String gettingStartedUnloadingFromTenDays;
    Double gettingStartedUnloadingFromTenDaysPrice;
    String gettingStartedUnloadingFromElevenFromNinetyNineDays;
    Double gettingStartedUnloadingFromElevenFromNinetyNineDaysPrice;

    String gettingStartedArrivalFromTenDays;
    Double gettingStartedArrivalFromTenDaysPrice;
    String gettingStartedArrivalFromElevenFromNinetyNineDays;
    Double gettingStartedArrivalFromElevenFromNinetyNineDaysPrice;

    private String carrierName;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ExcessiveUseOfContainerEntity that = (ExcessiveUseOfContainerEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
