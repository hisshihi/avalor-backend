package com.hiss.avalor_backend.util;

import lombok.Data;

import java.util.List;

@Data
public class AmoCRMLeadRequest {

    private String name; // Название сделки
    private Integer price; // Цена сделки

//    @JsonProperty("custom_fields_values")
//    private List<CustomField> customFieldsValues; // Пользовательские поля

    private List<Tag> tagsToAdd; // Теги для сделки

    // Класс для тегов
    @Data
    public static class Tag {
        private Integer id;
        private String name;
    }

    // Класс для пользовательских полей
//    @Data
//    public static class CustomField {
//        @JsonProperty("field_id")
//        private Long fieldId; // ID пользовательского поля
//
//        @JsonProperty("values")
//        private List<Value> values; // Значения пользовательского поля
//
//        @Data
//        public static class Value {
//            @JsonProperty("value")
//            private String value; // Конкретное значение
//        }
//    }
}

/*
* [
    {
        "name": "Сделка для примера 1",
        "price": 20000,
        "custom_fields_values": [
            {
                "field_id": 294471,
                "values": [
                    {
                        "value": "Наш первый клиент"
                    }
                ]
            }
        ],
        "tags_to_add": [
            {
              "name": "Первый тег"
            },
            {
              "id": 217261
            }
        ]
    }
]
* */