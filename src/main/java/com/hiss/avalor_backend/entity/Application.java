package com.hiss.avalor_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToMany(cascade = CascadeType.REMOVE)
//    @JoinTable(
//            name = "application_routes",
//            joinColumns = @JoinColumn(name = "application_id"),
//            inverseJoinColumns = @JoinColumn(name = "route_id")
//    )
//    private List<Route> routes;

    @ManyToMany
    @JoinTable(
            name = "application_additional_services",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "additional_service_id")
    )
    private List<AdditionalService> additionalServices;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "application_sea_routes",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "sea_route_id")
    )
    private List<RouteSea> seaRoutes;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "application_railway_routes",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "railway_route_id")
    )
    private List<RouteRailway> railwayRoutes;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "application_auto_routes",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "auto_route_id")
    )
    private List<RouteAuto> autoRoutes;


    private int totalCostRoute; // Примитивы вместо Integer
    private String applicationNumber;
    private String nameOfTheRecipient;
    private String innOfTheRecipient;
    private String addressOfTheRecipient;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image1;

    private String nameOfTheSender;

    @Email
    private String emailOfTheSender;

//    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneOfTheSender;

    private String fullNameOfTheSender;
    private String addressOfTheSender;
    private String invoice;
    private String nameOfTheProduct;

    private int quantityOfTheProduct;
    private int volumeOfTheProduct;

    private String cityFrom;
    private String cityTo;
    private String portOfArrival; // Порт прибытия
    private String placeDropOff; // Место drop off
    private String ship; // Судно
    private String typeOfEquipment; // Тип оборудования
    private String line; // Линия
    private String freightForwarder; // Экспедитор в порту
    @Column(length = 500)
    private String comment; // Дополительные комментарии

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image2;

    private int allTotalCost;

    @ManyToOne
    private UserEntity createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Application that = (Application) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
