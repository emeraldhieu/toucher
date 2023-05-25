package com.emeraldhieu.toucher.touch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteProviderTest {

    private final RouteProvider routeProvider = new RouteProvider();

    @BeforeEach
    public void setUp() {
        routeProvider.addRoute(new Route("StopA", "StopB", 4.5));
        routeProvider.addRoute(new Route("StopB", "StopC", 6.25));
        routeProvider.addRoute(new Route("StopA", "StopC", 8.45));
    }

    @Test
    public void givenNonexistentFromStopId_whenGetCharge_thenReturnsCharge() {
        // WHEN and THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeProvider.getCharge("nonexistent", "StopB");
        });
        assertEquals("Stop ID nonexistent not found", exception.getMessage());
    }

    @Test
    public void givenNonexistentToStopId_whenGetCharge_thenReturnsCharge() {
        // WHEN and THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeProvider.getCharge("StopA", "nonexistent");
        });
        assertEquals("Stop ID nonexistent not found", exception.getMessage());
    }

    @Test
    public void givenEmptyFromStopId_whenGetCharge_thenReturnsMaximumCharge() {
        // WHEN
        double charge = routeProvider.getCharge("", "StopB");

        // WHEN and THEN
        assertEquals(6.25, charge);
    }

    @Test
    public void givenEmptyToStopId_whenGetCharge_thenReturnsMaximumCharge() {
        // WHEN
        double charge = routeProvider.getCharge("StopA", "");

        // WHEN and THEN
        assertEquals(8.45, charge);
    }

    @Test
    public void givenTwoStopIds_whenGetCharge_thenReturnsCharge() {
        // WHEN
        double charge = routeProvider.getCharge("StopA", "StopB");

        // WHEN and THEN
        assertEquals(4.5, charge);
    }
}