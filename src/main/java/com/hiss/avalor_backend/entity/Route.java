package com.hiss.avalor_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityFrom;
    private String cityTo;
    private String transportType;
//    private int cost;

    private String polCountry; // Страна порта погрузки
    private String pol;        // Порт погрузки
    private String pod;        // Порт разгрузки
    @Transient
    private String carrierShortName; // Короткое название перевозчика
    private String eqpt;       // Тип оборудования
    private String containerTypeSize; // Тип и размер контейнера
    private LocalDate validTo; // Дата действия
    private String filo;       // Free In, Liner Out
    private String notes;      // Примечания
    private String comments;   // Комментарии

    private String arrangementForRailwayDays;
    private String transitTimeByTrainDays;
    private String totalWithoutMovementDays;
    private String totalTravelDays;
    private String totalTotalTimeDays;

    @ManyToOne
    @JoinColumn(name = "carrier_id", nullable = false)
    @ToString.Exclude
    private Carrier carrier;

    public String getCarrierShortName() {
        return carrier != null ? carrier.getName() : null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Route route = (Route) o;
        return getId() != null && Objects.equals(getId(), route.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
               "id = " + id + ", " +
               "cityFrom = " + cityFrom + ", " +
               "cityTo = " + cityTo + ", " +
               "transportType = " + transportType + ", " +
               "polCountry = " + polCountry + ", " +
               "pol = " + pol + ", " +
               "pod = " + pod + ", " +
               "carrierShortName = " + carrierShortName + ", " +
               "eqpt = " + eqpt + ", " +
               "containerTypeSize = " + containerTypeSize + ", " +
               "validTo = " + validTo + ", " +
               "filo = " + filo + ", " +
               "notes = " + notes + ", " +
               "comments = " + comments + ", " +
               "arrangementForRailwayDays = " + arrangementForRailwayDays + ", " +
               "transitTimeByTrainDays = " + transitTimeByTrainDays + ", " +
               "totalWithoutMovementDays = " + totalWithoutMovementDays + ", " +
               "totalTravelDays = " + totalTravelDays + ", " +
               "totalTotalTimeDays = " + totalTotalTimeDays + ")";
    }
}
