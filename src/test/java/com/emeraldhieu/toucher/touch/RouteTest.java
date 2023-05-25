package com.emeraldhieu.toucher.touch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {

    @Test
    public void givenNullFromStopId_whenConstruct_thenThrowsException() {
        // WHEN and THEN
        assertThrows(IllegalArgumentException.class,
            () -> new Route(null, "StopA", 3.14));
    }

    @Test
    public void givenNullToStopId_whenConstruct_thenThrowsException() {
        // WHEN and THEN
        assertThrows(IllegalArgumentException.class,
            () -> new Route("StopA", null, 3.14));
    }

    @Test
    public void givenEqualFromStopIdAndToStopId_whenConstruct_thenThrowsException() {
        // WHEN and THEN
        assertThrows(IllegalArgumentException.class,
            () -> new Route("StopA", "StopA", 3.14));
    }
}