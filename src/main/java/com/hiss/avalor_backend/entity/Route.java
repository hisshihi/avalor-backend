package com.hiss.avalor_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Route(String cityFrom, String cityTo, String pol, String pod, String carrier, String validTo, String transportType, String containerTypeSize, String eqpt, Integer filo, Integer exclusive) {
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.pol = pol;
        this.pod = pod;
        this.carrier = carrier;
        this.validTo = validTo;
        this.transportType = transportType;
        this.containerTypeSize = containerTypeSize;
        this.eqpt = eqpt;
        this.filo = filo;
        this.exclusive = exclusive;
    }

    public Route(String cityFrom, String cityTo, String pol, String pod, String carrier, String validTo, String transportType, String containerTypeSize, Integer exclusive, Integer filo20, Integer filo20HC, Integer filo40) {
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.pol = pol;
        this.pod = pod;
        this.carrier = carrier;
        this.validTo = validTo;
        this.transportType = transportType;
        this.containerTypeSize = containerTypeSize;
        this.exclusive = exclusive;
        this.filo20 = filo20;
        this.filo20HC = filo20HC;
        this.filo40 = filo40;
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
}
