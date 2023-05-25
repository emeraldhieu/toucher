package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An enum for trip's status.
 */
@Getter
@AllArgsConstructor
public enum TripStatus {

    COMPLETED("completed", "COMPLETED"),

    INCOMPLETE("incomplete", "INCOMPLETE"),

    CANCELLED("cancelled", "CANCELLED"),

    INVALID_TOUCH_TYPE("invalidTouchType", "Invalid touch type"),

    INVALID_STOP_ID("invalidStopId", "Invalid stop ID"),

    MISSING_PAN("missingPan", "Missing PAN");

    private String keyword;

    private String message;

    private static final Map<String, TripStatus> touchTypesByKeyword = new HashMap<>();

    static {
        List.of(TripStatus.values()).forEach(touchType ->
            touchTypesByKeyword.put(touchType.getKeyword(), touchType));
    }

    public static TripStatus forKeyword(String keyword) {
        return Optional.ofNullable(touchTypesByKeyword.get(keyword))
            .orElseThrow(() -> new IllegalArgumentException(String.format("%s '%s' not found.", TripStatus.class.getSimpleName(), keyword)));
    }

    public String getKeyword() {
        return keyword;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }
}
