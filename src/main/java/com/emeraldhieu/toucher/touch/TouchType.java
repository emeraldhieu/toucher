package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An enum for touch type. It can just be either ON or OFF.
 */
@Getter
@AllArgsConstructor
public enum TouchType {

    @JsonProperty("ON")
    ON("ON", "On"),

    @JsonProperty("ON")
    OFF("OFF", "Off");

    private String keyword;
    private String label;
    private static final Map<String, TouchType> touchTypesByKeyword = new HashMap<>();

    static {
        List.of(TouchType.values()).forEach(touchType ->
            touchTypesByKeyword.put(touchType.getKeyword(), touchType));
    }

    public static TouchType forKeyword(String keyword) {
        return Optional.ofNullable(touchTypesByKeyword.get(keyword))
            .orElseThrow(() -> new IllegalArgumentException(String.format("%s '%s' not found.", TouchType.class.getSimpleName(), keyword)));
    }

    public String toKeyword() {
        return keyword;
    }
}
