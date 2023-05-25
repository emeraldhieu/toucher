package com.emeraldhieu.toucher.touch;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
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

    private TouchProcessor touchProcessor;

    @BeforeEach
    public void setUp() {
        RouteProvider routeProvider = new RouteProvider();
        routeProvider.addRoute(new Route("StopA", "StopB", 4.5));
        routeProvider.addRoute(new Route("StopB", "StopC", 6.25));
        routeProvider.addRoute(new Route("StopA", "StopC", 8.45));
        touchProcessor = new TouchProcessor(routeProvider);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void givenCsvThatDoesNotHaveAnyRow_whenProcess_thenThrowsException() {
        // GIVEN
        File inputFile = createEmptyFile();
        File tripOutputFile = getTempFile();

        // WHEN and THEN
        assertThrows(TouchProcessorException.class,
            () -> touchProcessor.process(inputFile, tripOutputFile));
    }

    @Test
    public void givenCsvThatHasHeaderOnly_whenProcess_thenReturnCsvWithHeaderOnly() {
        // GIVEN
        String fileName = "headerOnly.csv";
        File inputFile = getInputFile(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        assertThrows(TouchProcessorException.class,
            () -> touchProcessor.process(inputFile, tripOutputFile));
    }

    @Test
    public void givenInvalidTouchType_whenProcess_thenReturnInvalidTouchType() {
        // GIVEN
        String fileName = "invalidTouchType.csv";
        File inputFile = getInputFile(fileName);
        String expectedFailureContent = getFailureContent(fileName);
        File failureOutputFile = getTempFile();

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
        String expectedFailureContent = getFailureContent(fileName);
        File failureOutputFile = getTempFile();

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureContent = getContent(failureOutputFile);
        assertEquals(expectedFailureContent, failureContent);
    }

    @Test
    public void givenTwoInvalidStopId_whenProcess_thenReturnInvalidStopId() {
        // GIVEN
        String fileName = "invalidStopIdAndOffTouch.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();
        String expectedFailureContent = getFailureContent(fileName);
        File failureOutputFile = getTempFile();
        File summaryOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile, failureOutputFile, summaryOutputFile);

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
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoSameKeyOnTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoSameKeyOnTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoSameKeyAndStopIdOnTouches_whenProcess_thenReturnTwoIncompleteTrip() {
        // GIVEN
        String fileName = "twoSameKeyAndStopIdOnTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoDifferentKeyOnTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoDifferentKeyOnTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenOneOffTouch_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "oneOffTouch.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoSameKeyOffTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoSameKeyOffTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoDifferentKeyOffTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoDifferentKeyOffTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenSameStopTouches_whenProcess_thenReturnOneCancelledTrip() {
        // GIVEN
        String fileName = "twoSameStopTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoPairsOfSameStopTouches_whenProcess_thenReturnTwoCancelledTrips() {
        // GIVEN
        String fileName = "twoPairsOfSameStopTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenAAndB_whenProcess_thenReturnBetweenAAndB() {
        // GIVEN
        String fileName = "betweenAAndB.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenBAndA_whenProcess_thenReturnBetweenAAndB() {
        // GIVEN
        String fileName = "betweenBAndA.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenBAndC_whenProcess_thenReturnBetweenBAndC() {
        // GIVEN
        String fileName = "betweenBAndC.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenCAndB_whenProcess_thenReturnBetweenCAndB() {
        // GIVEN
        String fileName = "betweenCAndB.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenAAndC_whenProcess_thenReturnBetweenAAndC() {
        // GIVEN
        String fileName = "betweenAAndC.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenBetweenCAndA_whenProcess_thenReturnBetweenCAndA() {
        // GIVEN
        String fileName = "betweenCAndA.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void twoDifferentKeyTouches_whenProcess_thenReturnTwoIncompleteTrips() {
        // GIVEN
        String fileName = "twoDifferentKeyTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoTrips_whenProcess_thenReturnTwoTrips() {
        // GIVEN
        String fileName = "twoTrips.csv";
        File inputFile = getInputFile(fileName);
        String expectedTripContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String tripContent = getContent(tripOutputFile);
        assertEquals(expectedTripContent, tripContent);
    }

    @Test
    public void givenTwoTrips_whenProcessSummary_thenReturnSummary() {
        // GIVEN
        String fileName = "twoTrips.csv";
        File inputFile = getInputFile(fileName);
        String expectedSummaryContent = getSummaryContent(fileName);
        File summaryOutputFile = getTempFile();

        // WHEN
        touchProcessor.processSummary(inputFile, summaryOutputFile);

        // THEN
        String summaryContent = getContent(summaryOutputFile);
        assertEquals(expectedSummaryContent, summaryContent);
    }

    @Test
    public void givenFoo_whenProcessSummary_thenReturnBar() {
        // GIVEN
        String fileName = "touchDataImproved.csv";
        File inputFile = getInputFile(fileName);
        String expectedOutputContent = getTripContent(fileName);
        File tripOutputFile = getTempFile();

        // WHEN
        touchProcessor.process(inputFile, tripOutputFile);

        // THEN
        String summaryContent = getContent(tripOutputFile);
        assertEquals(expectedOutputContent, summaryContent);
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
}