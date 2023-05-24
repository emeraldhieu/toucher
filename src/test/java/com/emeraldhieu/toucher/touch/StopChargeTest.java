package com.emeraldhieu.toucher.touch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StopChargeTest {

    @Test
    public void givenEqualFromStopIdAndToStopId_whenConstruct_thenThrowsException() {
        // WHEN and THEN
        assertThrows(IllegalArgumentException.class, () -> {
            new StopCharge("a", "a", 3.14);
        });
    }

    @Test
    public void givenNonexistentFromStopId_whenGetCharge_thenReturnsCharge() {
        // WHEN and THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            StopCharge.getCharge("nonexistent", "StopB");
        });
        assertEquals("Stop ID nonexistent not found", exception.getMessage());
    }

    @Test
    public void givenNonexistentToStopId_whenGetCharge_thenReturnsCharge() {
        // WHEN and THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            StopCharge.getCharge("StopA", "nonexistent");
        });
        assertEquals("Stop ID nonexistent not found", exception.getMessage());
    }

    @Test
    public void givenEmptyFromStopId_whenGetCharge_thenReturnsCharge() {
        // WHEN
        double charge = StopCharge.getCharge("", "StopB");

        // WHEN and THEN
        assertEquals(6.25, charge);
    }

    @Test
    public void givenEmptyToStopId_whenGetCharge_thenReturnsCharge() {
        // WHEN
        double charge = StopCharge.getCharge("StopA", "");

        // WHEN and THEN
        assertEquals(8.45, charge);
    }

    @Test
    public void givenStopAAndB_whenGetCharge_thenReturnsCharge() {
        // WHEN
        double charge = StopCharge.getCharge("StopA", "StopB");

        // WHEN and THEN
        assertEquals(4.5, charge);
    }

    @Test
    public void givenStopBAndC_whenGetCharge_thenReturnsCharge() {
        // WHEN
        double charge = StopCharge.getCharge("StopB", "StopC");

        // WHEN and THEN
        assertEquals(6.25, charge);
    }

    @Test
    public void givenStopAAndC_whenGetCharge_thenReturnsCharge() {
        // WHEN
        double charge = StopCharge.getCharge("StopA", "StopC");

        // WHEN and THEN
        assertEquals(8.45, charge);
    }
}