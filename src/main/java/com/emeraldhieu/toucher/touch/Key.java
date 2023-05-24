package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Key {
    @JsonProperty("Date")
    @EqualsAndHashCode.Include
    private final String date;

    @JsonProperty("CompanyId")
    @EqualsAndHashCode.Include
    private final String companyId;

    @JsonProperty("BusId")
    @EqualsAndHashCode.Include
    private final String busId;

    public static Key from(String date, String companyId, String busId) {
        return new Key(date, companyId, busId);
    }
}