package com.emeraldhieu.toucher.touch;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * A class that represents a route that connects an origin to a destination.
 */
@Getter
public class Route {

    private final String fromStopId;
    private final String toStopId;
    private final double charge;

    public Route(String fromStopId, String toStopId, double charge) {
        if (fromStopId == null) {
            throw new IllegalArgumentException("fromStopId must not be null");
        }
        if (toStopId == null) {
            throw new IllegalArgumentException("toStopId must not be null");
        }
        if (fromStopId.equals(toStopId)) {
            throw new IllegalArgumentException("fromStopId and toStopId must not be equal");
        }
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.charge = charge;
    }

    public boolean containsStop(String stopId) {
        return List.of(fromStopId, toStopId).contains(stopId);
    }

    public boolean containsStop(String fromStopId, String toStopId) {
        return containsStop(fromStopId) && containsStop(toStopId);
    }
}

