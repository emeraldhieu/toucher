package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@JsonPropertyOrder(value = {"date", "companyId", "busId",
    "completeTripCount", "incompleteTripCount", "cancelledTripCount", "totalCharges"})
public class SummaryLine {

    @JsonProperty("Date")
    private final String date;

    @JsonProperty("CompanyId")
    private final String companyId;

    @JsonProperty("BusId")
    private final String busId;

    @JsonProperty("CompleteTripCount")
    @Builder.Default
    private final int completeTripCount = 0;

    @JsonProperty("IncompleteTripCount")
    @Builder.Default
    private final int incompleteTripCount = 0;

    @JsonProperty("CancelledTripCount")
    @Builder.Default
    private final int cancelledTripCount = 0;

    @JsonProperty("TotalCharges")
    @Builder.Default
    private final double totalCharges = 0;

    @Getter
    @Builder(toBuilder = true)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Key {
        @JsonProperty("Date")
        @EqualsAndHashCode.Include
        private final String date;

        @JsonProperty("CompanyId")
        @EqualsAndHashCode.Include
        private final String companyId;

        @JsonProperty("BusId")
        @EqualsAndHashCode.Include
        private final String busId;
    }
}
