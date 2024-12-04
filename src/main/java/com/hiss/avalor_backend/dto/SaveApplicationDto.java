package com.hiss.avalor_backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveApplicationDto {

    @NotEmpty(message = "Route IDs must not be empty")
    private List<Long> ids = new ArrayList<>();

    @NotNull(message = "Total cost of route must not be null")
    @Min(value = 0, message = "Total cost must be non-negative")
    private Integer totalCostRoute;

    private String applicationNumber;

    @NotEmpty(message = "Recipient name is required")
    private String nameOfTheRecipient;

    @NotEmpty(message = "Recipient INN is required")
    private String innOfTheRecipient;

    private String addressOfTheRecipient;

    private MultipartFile image1; // Вместо byte[] используем MultipartFile

    private String nameOfTheSender;

    @Email(message = "Invalid email format")
    private String emailOfTheSender;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneOfTheSender;

    private String fullNameOfTheSender;
    private String addressOfTheSender;
    private String invoice;
    private String nameOfTheProduct;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantityOfTheProduct;

    private Integer volumeOfTheProduct;

    private MultipartFile image2;

    private List<Long> additionalServiceIds;

    @Min(value = 0, message = "Total cost must be non-negative")
    private Integer allTotalCost;
}

