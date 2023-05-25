package com.emeraldhieu.toucher.touch;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TouchProcessorTest {

    private TouchProcessorWrapper touchProcessor;

    @BeforeEach
    public void setUp() {
        RouteProvider routeProvider = new RouteProvider();
        routeProvider.addRoute(new Route("StopA", "StopB", 4.5));
        routeProvider.addRoute(new Route("StopB", "StopC", 6.25));
        routeProvider.addRoute(new Route("StopA", "StopC", 8.45));
        touchProcessor = new TouchProcessorWrapper(new TouchProcessor(routeProvider));
    }

    @Test
    public void givenCsvThatDoesNotHaveAnyRow_whenProcess_thenThrowsException() {
        // GIVEN
        File inputFile = createEmptyFile();
        File tripOutputFile = getTempFile();

        // WHEN and THEN
        assertThrows(TouchProcessorException.class,
            () -> touchProcessor.processTrip(inputFile, tripOutputFile));
    }

    @Test
    public void givenCsvThatHasHeaderOnly_whenProcess_thenThrowsException() {
        // GIVEN
        String fileName = "headerOnly.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        assertThrows(TouchProcessorException.class,
            () -> touchProcessor.processTrip(inputFile, tripOutputFile));
    }

    @Test
    public void givenInvalidTouchType_whenProcess_thenReturnInvalidTouchType() {
        // GIVEN
        String fileName = "invalidTouchType.csv";
        File inputFile = getInputFile(fileName);
        File failureOutputFile = getTempFile();
        String expectedFailureContent = getFailureContent(fileName);

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureContent = getContent(failureOutputFile);
        assertEquals(expectedFailureContent, failureContent);
    }

    @Test
    public void givenMissingPan_whenProcess_thenReturnMissingPan() {
        // GIVEN
        String fileName = "missingPan.csv";
        File inputFile = getInputFile(fileName);
        File failureOutputFile = getTempFile();
        String expectedFailureContent = getFailureContent(fileName);

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureContent = getContent(failureOutputFile);
        assertEquals(expectedFailureContent, failureContent);
    }

    @Test
    public void givenInvalidStopId_whenProcess_thenReturnInvalidStopId() {
        // GIVEN
        String fileName = "invalidStopId.csv";
        File inputFile = getInputFile(fileName);
        File failureOutputFile = getTempFile();
        String expectedFailureContent = getFailureContent(fileName);

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureContent = getContent(failureOutputFile);
        assertEquals(expectedFailureContent, failureContent);
    }

    @Test
    public void givenInvalidStopIdAndOffTouch_whenProcess_thenReturnInvalidStopId() {
        // GIVEN
        String fileName = "invalidStopIdAndOffTouch.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);
        File failureOutputFile = getTempFile();
        String expectedFailureContent = getFailureContent(fileName);

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile, failureOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
        String failureContent = getContent(failureOutputFile);
        assertEquals(expectedFailureContent, failureContent);
    }

    @Test
    public void givenOneOnTouch_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "oneOnTouch.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoSameKeyAndDifferentStopIdOnTouches_whenProcess_thenReturnTwoIncompleteTrips() {
        // GIVEN
        String fileName = "twoSameKeyAndDifferentStopIdOnTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoSameKeyAndStopIdOnTouches_whenProcess_thenReturnTwoIncompleteTrip() {
        // GIVEN
        String fileName = "twoSameKeyAndStopIdOnTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoDifferentKeyOnTouches_whenProcess_thenReturnTwoIncompleteTrips() {
        // GIVEN
        String fileName = "twoDifferentKeyOnTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenOneOffTouch_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "oneOffTouch.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoSameKeyOffTouches_whenProcess_thenReturnTwoIncompleteTrips() {
        // GIVEN
        String fileName = "twoSameKeyAndDifferentStopIdOffTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoDifferentKeyOffTouches_whenProcess_thenReturnTwoIncompleteTrips() {
        // GIVEN
        String fileName = "twoDifferentKeyOffTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenSameStopTouches_whenProcess_thenReturnOneCancelledTrip() {
        // GIVEN
        String fileName = "twoSameStopTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoPairsOfSameStopTouches_whenProcess_thenReturnTwoCancelledTrips() {
        // GIVEN
        String fileName = "twoPairsOfSameStopTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenAAndB_whenProcess_thenReturnBetweenAAndB() {
        // GIVEN
        String fileName = "betweenAAndB.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenBAndA_whenProcess_thenReturnBetweenAAndB() {
        // GIVEN
        String fileName = "betweenBAndA.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void twoDifferentKeyTouches_whenProcess_thenReturnTwoIncompleteTrips() {
        // GIVEN
        String fileName = "twoDifferentKeyAndTouchTypeTouches.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoTrips_whenProcess_thenReturnTwoTrips() {
        // GIVEN
        String fileName = "twoTrips.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();
        String expectedTripContent = getTripContent(fileName);

        // WHEN
        touchProcessor.processTrip(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoTrips_whenProcessSummary_thenReturnSummary() {
        // GIVEN
        String fileName = "twoTrips.csv";
        File inputFile = getInputFile(fileName);
        File summaryOutputFile = getTempFile();
        String expectedSummaryContent = getSummaryContent(fileName);

        // WHEN
        touchProcessor.processSummary(inputFile, summaryOutputFile);

        // THEN
        String summaryContent = getContent(summaryOutputFile);
        assertEquals(expectedSummaryContent, summaryContent);
    }

    @SneakyThrows
    private File createEmptyFile() {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);
        FileUtils.write(tempFile, "", "UTF-8");
        tempFile.deleteOnExit();
        return tempFile;
    }

    @SneakyThrows
    private File getInputFile(String fileName) {
        String inputDirectory = "csv/touchData";
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);
        tempFile.deleteOnExit();

        String filePath = Path.of(inputDirectory, "/", fileName).toString();
        try (InputStream inputStream = TouchProcessorTest.class.getClassLoader()
            .getResourceAsStream(filePath);
             FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            inputStream.transferTo(fileOutputStream);
            return tempFile;
        }
    }

    private String getTripContent(String fileName) {
        String directory = "csv/trip";
        return getFileContent(directory, fileName);
    }

    private String getFailureContent(String fileName) {
        String directory = "csv/failure";
        return getFileContent(directory, fileName);
    }

    private String getSummaryContent(String fileName) {
        String directory = "csv/summary";
        return getFileContent(directory, fileName);
    }

    private String getFileContent(String relativePath, String fileName) {
        try (InputStream inputStream = TouchProcessorTest.class.getClassLoader()
            .getResourceAsStream(Path.of(relativePath, fileName).toString());
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return bufferedReader.lines()
                .collect(Collectors.joining("")); // Collect lines.
            //.replaceAll("\\s{2,}", " "); // Remove redundant white spaces
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't read file", e);
        }
    }

    @SneakyThrows
    private File getTempFile() {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);
        tempFile.deleteOnExit();
        return tempFile;
    }

    @SneakyThrows
    private String getContent(File file) {
        return Files.readAllLines(file.toPath()).stream()
            .collect(Collectors.joining(""));
    }

    /**
     * A wrapper for {@link TouchProcessor} that reduces passing redundant arguments for test methods.
     */
    private class TouchProcessorWrapper {
        private final TouchProcessor touchProcessor;

        public TouchProcessorWrapper(TouchProcessor touchProcessor) {
            this.touchProcessor = touchProcessor;
        }

        public void processTrip(File inputFile, File tripOutputFile) {
            process(inputFile, tripOutputFile, getTempFile());
        }

        public void processFailure(File inputFile, File failureOutputFile) {
            process(inputFile, getTempFile(), failureOutputFile);
        }

        public void processSummary(File inputFile, File summaryOutputFile) {
            process(inputFile, getTempFile(), getTempFile(), summaryOutputFile);
        }

        public void process(File inputFile, File tripOutputFile, File failureOutputFile) {
            process(inputFile, tripOutputFile, failureOutputFile, getTempFile());
        }

        public void process(File inputFile, File tripOutputFile, File failureOutputFile, File summaryOutputFile) {
            touchProcessor.process(inputFile, tripOutputFile, failureOutputFile, summaryOutputFile);
        }
    }
}