package com.hiss.avalor_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentContactDto {

    @JsonProperty("rndnumber")
    String rndnumber = generateNumber();
    @JsonProperty("typecompanyandname")
    String typecompanyandname;
    @JsonProperty("sname")
    String sname;
    @JsonProperty("username")
    String username;
    @JsonProperty("condition")
    String condition;
    @JsonProperty("INN1")
    String INN1;
    @JsonProperty("fulladress1")
    String fulladress1;
    @JsonProperty("INN2")
    String INN2;
    @JsonProperty("fulladress2")
    String fulladress2;
    @JsonProperty("INN")
    String INN;
    @JsonProperty("KPP")
    String KPP;
    @JsonProperty("OGRN")
    String OGRN;
    @JsonProperty("R")
    String R;
    @JsonProperty("S1")
    String S1;
    @JsonProperty("K")
    String K;
    @JsonProperty("S2")
    String S2;
    @JsonProperty("fullnamebank")
    String fullnamebank;
    @JsonProperty("BIK")
    String BIK;
    @JsonProperty("telephone")
    String telephone;
    @JsonProperty("email")
    String email;

    public String getInnKpp() {
        return INN + "/" + KPP;
    }

    public String getRS1() {
        return R + "/" + S1;
    }

    public String getKS2() {
        return R + "/" + S2;
    }

    private String generateNumber() {
        String uniqueId = String.format("%08d", System.currentTimeMillis() % 1000000);
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        return uniqueId + "/" + currentMonth;
    }

}
