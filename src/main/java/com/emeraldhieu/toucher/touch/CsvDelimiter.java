package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum CsvDelimiter {
    COMMA(','),
    SEMICOLON(';'),
    TILDE('~'),
    TAB('\t');

    private final Character keyword;

    private static final Map<String, CsvDelimiter> csvDelimitersByKeyword;

    static {
        csvDelimitersByKeyword = new HashMap<>();
        Arrays.stream(CsvDelimiter.values())
            .forEach(csvDelimiter -> csvDelimitersByKeyword.put(String.valueOf(csvDelimiter.getKeyword()), csvDelimiter));
    }

    /**
     * @throws IllegalArgumentException If keyword is invalid.
     */
    @JsonCreator
    public static CsvDelimiter forKeyword(String keyword) {
        return Optional.ofNullable(csvDelimitersByKeyword.get(keyword))
            .orElseThrow(() -> new IllegalArgumentException(String.format("CSV delimiter '%s' not found.", keyword)));
    }

    @JsonValue
    public Character toKeyword() {
        return keyword;
    }
}
