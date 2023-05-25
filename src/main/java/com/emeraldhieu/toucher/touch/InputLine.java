package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class that represents an input line from "touch data".
 */
@Getter
@Builder(toBuilder = true)
@JsonPropertyOrder(value = {"id", "dateTimeUtc", "touchType", "stopId", "companyId", "busId", "pan"})
@Jacksonized
public class InputLine {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @JsonAlias("ID")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String id;

    @JsonAlias("DateTimeUTC")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String dateTimeUtc;

    @JsonAlias("TouchType")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String touchType;

    private final TouchType touchTypeEnum;

    @JsonAlias("StopID")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String stopId;

    @JsonAlias("CompanyID")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String companyId;

    @JsonAlias("BusID")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String busId;

    @JsonAlias("PAN")
    @JsonDeserialize(using = StringDeserializer.class)
    private final String pan;

    @JsonIgnore
    public Key getKey() {
        return Key.from(getDate(dateTimeUtc), companyId, busId);
    }

    @JsonIgnore
    private String getDate(String utcDateTime) {
        LocalDateTime dateTime = LocalDateTime.parse(utcDateTime, dateTimeFormatter);
        return dateFormatter.format(dateTime);
    }
}
