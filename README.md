Все конечные точки начинаются с домена и дальше сама конечная точка
Пример: http://localhost:8080/delivery, поэтому домен будет усечён

конечные точки с id - id должен передаваться как параметр url. Пример - api/carrier/3 где 3 это id поставщика

GET api/delivery/calculate - доступна всем
Пример
запроса - http://localhost:8080/api/delivery/calculate?cityFrom=Москва&cityTo=Владивосток&time=2024-12-12&weight=40HQ

Возвращает массив поделённый на сегменты, сегмент это маршрут

POST api/delivery/calculate - только для админа
Принимает json с такими данными

private String cityFrom;
private String cityTo;
private String transportType;

    private String polCountry; // Страна порта погрузки
    private String pol;        // Порт погрузки
    private String pod;        // Порт разгрузки

// private String carrierShortName; // Короткое название перевозчика
private String eqpt; // Тип оборудования
private String containerTypeSize; // Тип и размер контейнера
private LocalDate validTo; // Дата действия
private String filo; // Free In, Liner Out
private String notes; // Примечания
private String comments; // Комментарии

    private Long carrierId;

    private String arrangementForRailwayDays;
    private String transitTimeByTrainDays;
    private String totalWithoutMovementDays;
    private String totalTravelDays;
    private String totalTotalTimeDays;

GET api/delivery/admin - только для админа.
Возвращает json заключенный в page
http://localhost:8080/api/delivery/all?page=0&size=5

PATH api/delivery/id маршрута - только для админа
Принимает json
{
"cityFrom": "СПБ",
"cityTo": "Казань",
"transportType": "ЖД",
"polCountry": "Россия",
"pol": "Москва-Товарная",
"pod": "Казань-ЖД",
"eqpt": "40HQ",
"containerTypeSize": "COC",
"validTo": "2025-12-31",
"filo": "Liner Out",
"notes": "Прямой поезд",
"comments": "Часто задержки",
"carrierId": 1,
"arrangementForRailwayDays": "15",
"transitTimeByTrainDays": "25",
"totalWithoutMovementDays": "0",
"totalTravelDays": "Нет данных",
"totalTotalTimeDays": "Нет данных"

}

DELETE api/delivery/id - только для админа

Заявки
POST api/application - для всех авторизированных пользователей
Принимает from-data
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

GET api/application?page=0&size=3 - для всех авторизованных
Возвращает массив заявок обёрнутых в page

GET api/application/admin?page=0&size=3 - доступно только админу
Возвращает все заявки пользователей

PATH api/application/id - для администратора, редактирование любых полей
Возвращает обновлённую заявку
Принимает form-data:
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

    @Size(min = 1)
    private List<Long> ids; // IDs маршрутов

    @Size(min = 1)
    private List<Long> additionalServiceIds; // IDs дополнительных услуг

PATH api/application/user/id - для всех авторизованных. Пользователь может обновлять только свои заявки, иначе будет ошибка
Принимает те же данные, что и запрос для админа на обновление

DELETE api/application/user/id - для всех авторизованных. Пользователь может удалять только свои заявки, иначе также ошибка

DELETE api/application/id - для админа

Поставщик
POST /api/carrier - только для админа
Принимает json:
private String name;
private Integer price;
private Integer priceDollars;
private Integer containerRentalPrice;
private Integer containerRentalPriceDollars;

GET /api/carrier - для всех авторизированных
Возвращает json массив

PATH /api/carrier/id - принимает тот же json, что и POST. Только для админа

DELETE /api/carrier/id - только для админа

Данные о хранении контейнеров

Для POST, GET будет - /api/excessive-use-of-container-entity
Для PATH, DELETE - /api/excessive-use-of-container-entity/id

POST /api/excessive-use-of-container-entity
Принимает json
String gettingStartedIssuanceFromSevenDays;
String gettingStartedIssuanceFromEightFromNinetyNineDays;

    String gettingStartedUnloadingFromTenDays;
    String gettingStartedUnloadingFromElevenFromNinetyNineDays;

    String gettingStartedArrivalFromTenDays;
    String gettingStartedArrivalFromElevenFromNinetyNineDays;

PATH принимает те же данные, что и POST

GET возвращает массив json

Вторые данные о контейнерах
Для POST, GET - /api/storage-at-the-port-of-arrival
Для PATH, DELETE - /api/storage-at-the-port-of-arrival/id

POST, PATH принимают
private String toTenDays;
private String fromElevenToTwentyOneDays;
private String fromTwentyTwoToNinetyNineDays;
