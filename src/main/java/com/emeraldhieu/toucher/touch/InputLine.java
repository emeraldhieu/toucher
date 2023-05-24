package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * A class that represents an input line from "touch data".
 */
@Getter
@Builder(toBuilder = true)
@JsonPropertyOrder(value = {"id", "dateTimeUtc", "touchType", "stopId", "companyId", "busId", "pan"})
@Jacksonized
public class InputLine {

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
}
