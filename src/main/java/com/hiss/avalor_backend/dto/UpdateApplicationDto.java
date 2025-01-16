package com.hiss.avalor_backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateApplicationDto {

    private String nameOfTheRecipient;
    private String innOfTheRecipient;
    private String addressOfTheRecipient;
    private MultipartFile image1;

    private String nameOfTheSender;
    private String emailOfTheSender;
    private String phoneOfTheSender;
    private String fullNameOfTheSender;
    private String addressOfTheSender;
    private MultipartFile image2;

    private String invoice;
    private String nameOfTheProduct;
    private Integer quantityOfTheProduct;
    private Integer volumeOfTheProduct;
    private Integer totalCostRoute;
    private Integer allTotalCost;

//    @Size(min = 1)
//    private List<Long> ids; // IDs маршрутов

    @Size(min = 1)
    private List<Long> seaRouteIds;
    @Size(min = 1)
    private List<Long> railwayIds;
    @Size(min = 1)
    private List<Long> autoRouteIds;

    @Size(min = 1)
    private List<Long> additionalServiceIds; // IDs дополнительных услуг

}
