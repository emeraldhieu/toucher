package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Builder
@Getter
@JsonPropertyOrder(value = {"started", "finished", "durationSeconds",
    "fromStopId", "toStopId", "chargeAmount", "companyId", "busId", "hashedPan", "tripStatus"})
public class ResultLine {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @JsonProperty("Started")
    private final String started;

    @JsonProperty("Finished")
    private final String finished;

    @JsonProperty("FromStopId")
    private final String fromStopId;

    @JsonProperty("ToStopId")
    private final String toStopId;

    @JsonProperty("ChargeAmount")
    private final Double chargeAmount;

    @JsonProperty("CompanyId")
    private final String companyId;

    @JsonProperty("BusId")
    private final String busId;

    @JsonProperty("HashedPan")
    private final String hashedPan;

    @JsonProperty("Status")
    private final TripStatus tripStatus;

    @JsonProperty("DurationSec")
    public Long getDurationSeconds() {
        LocalDateTime startedTime = Optional.ofNullable(started)
            .map(nonNullStarted -> LocalDateTime.parse(started, dateTimeFormatter))
            .orElse(null);
        LocalDateTime finishedTime = Optional.ofNullable(finished)
            .map(nonNullFinished -> LocalDateTime.parse(finished, dateTimeFormatter))
            .orElse(null);
        if (startedTime == null || finishedTime == null) {
            return null;
        }
        return ChronoUnit.SECONDS.between(startedTime, finishedTime);
    }

    @JsonIgnore
    public Key getKey() {
        return Key.from(getSummaryDate(), companyId, busId);
    }

    @JsonIgnore
    public String getSummaryDate() {
        // Assume the summary's date is started date
        LocalDateTime dateTime = started != null
            ? LocalDateTime.parse(started, dateTimeFormatter)
            : LocalDateTime.parse(finished, dateTimeFormatter);
        return dateFormatter.format(dateTime);
    }
}
