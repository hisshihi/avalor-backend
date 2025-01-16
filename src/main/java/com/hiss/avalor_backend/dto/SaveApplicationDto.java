package com.hiss.avalor_backend.dto;

import jakarta.persistence.Column;
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

//    @NotEmpty(message = "Route IDs must not be empty")
//    private List<Long> ids = new ArrayList<>(); // ids маршрутов

    private List<Long> seaRouteIds = new ArrayList<>();
    private List<Long> railwayIds = new ArrayList<>();
    private List<Long> autoRouteIds = new ArrayList<>();

//    @NotNull(message = "Total cost of route must not be null")
//    @Min(value = 0, message = "Total cost must be non-negative")
    private Integer totalCostRoute; // Цена маршрута

    private String applicationNumber; // Номер заявки

//    @NotEmpty(message = "Recipient name is required")
    private String nameOfTheRecipient; // Имя получателя

//    @NotEmpty(message = "Recipient INN is required")
    private String innOfTheRecipient; // Инн получателя

    private String addressOfTheRecipient; // Адрес полчателя

    private MultipartFile image1; // Изображение 1

    private String nameOfTheSender; // Имя отправителя

//    @Email(message = "Invalid email format")
    private String emailOfTheSender; // Почта отправителя

//    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneOfTheSender; // Номер телефона отправителя

    private String fullNameOfTheSender; // Полное имя Отправиетля
    private String addressOfTheSender; // Адерс отправителя
    private String invoice; // Инвойс
    private String nameOfTheProduct; // Название товара

//    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantityOfTheProduct; // Кол-во товара

    private Integer volumeOfTheProduct; // Объём товара

    private MultipartFile image2; // Изображение 2(товар)

    private List<Long> additionalServiceIds = new ArrayList<>(); // ids доп услуг

//    @Min(value = 0, message = "Total cost must be non-negative")
    private Integer allTotalCost; // Общая(конечная) стоимость

    private String cityFrom; // Город откуда
    private String cityTo; // Город куда
    private String portOfArrival; // Порт прибытия
    private String placeDropOff; // Место drop off
    private String ship; // Судно
    private String typeOfEquipment; // Тип оборудования
    private String line; // Линия
    private String freightForwarder; // Экспедитор в порту
    @Column(length = 500)
    private String comment; // Дополительные комментарии

}

