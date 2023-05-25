package com.emeraldhieu.toucher.touch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A class that contains and provides possible routes that are validated upon being added.
 */
public class RouteProvider {

    private final List<Route> routes = new ArrayList<>();

    public void addRoute(Route route) {
        routes.add(route);
    }

    private void doValidateStopId(String stopId) {
        if (routes.stream()
            .allMatch(route -> !stopId.equals("") && !route.containsStop(stopId))) {
            throw new IllegalArgumentException("Stop ID %s not found".formatted(stopId));
        }
    }

    public void validateStopId(String stopId) {
        doValidateStopId(stopId);
    }

    public double getCharge(String fromStopId, String toStopId) {
        doValidateStopId(fromStopId);
        doValidateStopId(toStopId);

        if (fromStopId.equals("")) {
            return routes.stream()
                .filter(route -> route.containsStop(toStopId))
                .max(Comparator.comparingDouble(o -> o.getCharge()))
                .map(route -> route.getCharge())
                .get();
        }

        if (toStopId.equals("")) {
            return routes.stream()
                .filter(route -> route.containsStop(fromStopId))
                .max(Comparator.comparingDouble(o -> o.getCharge()))
                .map(route -> route.getCharge())
                .get();
        }

        return routes.stream()
            .filter(route -> route.containsStop(fromStopId, toStopId))
            .map(route -> {
                if (!fromStopId.equals(toStopId)) { // Different ends
                    return route.getCharge();
                }
                return 0d; // Cancelled
            })
            .findFirst()
            .get(); // Be confident to get because of pre-check validations
    }
}
