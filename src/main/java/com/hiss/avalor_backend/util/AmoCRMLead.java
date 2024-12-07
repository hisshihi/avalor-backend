package com.hiss.avalor_backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AmoCRMLead {

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("responsible_user_id") // ID ответственного пользователя
    private Integer responsibleUserId;

    @JsonProperty("status_id")         // ID статуса сделки
    private Integer statusId;

    @JsonProperty("pipeline_id")       // ID воронки
    private Integer pipelineId;

    @JsonProperty("custom_fields_values") // Пользовательские поля
    private List<CustomField> customFieldsValues;

    // ... другие поля по необходимости


    // Вложенный класс для пользовательских полей (пример)
    @Data
    public static class CustomField {
        @JsonProperty("field_id")
        private Long fieldId;
        @JsonProperty("values")
        private List<Value> values;


        @Data
        public static class Value{

            @JsonProperty("value")
            private String value;
        }
    }
}
