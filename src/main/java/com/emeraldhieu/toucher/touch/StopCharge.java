package com.emeraldhieu.toucher.touch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A class that validates fromStopId, toStopId, and calculates the corresponding charge.
 */
public class StopCharge {

    private final String fromStopId;
    private final String toStopId;
    private final double charge;
    private static final List<StopCharge> stopCharges = new ArrayList<>();

    /**
     * In real use case, we can inverse control of this list (IoC).
     */
    static {
        stopCharges.add(new StopCharge("StopA", "StopB", 4.5));
        stopCharges.add(new StopCharge("StopB", "StopC", 6.25));
        stopCharges.add(new StopCharge("StopA", "StopC", 8.45));
    }

    public StopCharge(String fromStopId, String toStopId, double charge) {
        if (fromStopId.equals(toStopId)) {
            throw new IllegalArgumentException("fromStopId and toStopId must not be equal");
        }
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.charge = charge;
    }

    public static void validateStopId(String stopId) {
        if (stopCharges.stream()
            .allMatch(stopCharge -> !stopId.equals("") && !stopCharge.contains(stopId))) {
            throw new IllegalArgumentException("Stop ID %s not found".formatted(stopId));
        }
    }

    public static double getCharge(String fromStopId, String toStopId) {
        validateStopId(fromStopId);
        validateStopId(toStopId);

        if (fromStopId.equals("")) {
            return stopCharges.stream()
                .filter(stopPair -> stopPair.contains(toStopId))
                .max(Comparator.comparingDouble(o -> o.charge))
                .map(stopCharge -> stopCharge.charge)
                .get();
        }

        if (toStopId.equals("")) {
            return stopCharges.stream()
                .filter(stopPair -> stopPair.contains(fromStopId))
                .max(Comparator.comparingDouble(o -> o.charge))
                .map(stopCharge -> stopCharge.charge)
                .get();
        }

        return stopCharges.stream()
            .filter(stopCharge -> stopCharge.contains(fromStopId, toStopId))
            .map(stopCharge -> {
                if (!fromStopId.equals(toStopId)) { // Different ends
                    return stopCharge.charge;
                }
                return 0d; // Cancelled
            })
            .findFirst()
            .get(); // Be confident to get because of pre-check validations
    }

    private boolean contains(String stopId) {
        return List.of(fromStopId, toStopId).contains(stopId);
    }

    private boolean contains(String fromStopId, String toStopId) {
        return contains(fromStopId) && contains(toStopId);
    }
}

