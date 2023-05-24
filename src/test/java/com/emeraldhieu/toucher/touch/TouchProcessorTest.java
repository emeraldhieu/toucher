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
        touchProcessor = new TouchProcessor();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void givenCsvThatDoesNotHaveAnyRow_whenProcess_thenThrowsException() {
        // GIVEN
        File inputFile = createEmptyFile();
        File outputFile = getOutputFile();

        // WHEN and THEN
        assertThrows(TouchProcessorException.class,
            () -> touchProcessor.process(inputFile, outputFile));
    }

    @Test
    public void givenCsvThatHasHeaderOnly_whenProcess_thenReturnCsvWithHeaderOnly() {
        // GIVEN
        String fileName = "headerOnly.csv";
        File inputFile = getInputFile(fileName);
        File outputFile = getOutputFile();

        // WHEN
        assertThrows(TouchProcessorException.class,
            () -> touchProcessor.process(inputFile, outputFile));
    }

    @Test
    public void givenInvalidTouchType_whenProcess_thenReturnInvalidTouchType() {
        // GIVEN
        String fileName = "invalidTouchType.csv";
        File inputFile = getInputFile(fileName);
        String expectedFailureOutputContent = getFailureOutputContent(fileName);
        File failureOutputFile = getOutputFile();

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureOutputContent = getOutputContent(failureOutputFile);
        assertEquals(expectedFailureOutputContent, failureOutputContent);
    }

    @Test
    public void givenInvalidStopId_whenProcess_thenReturnInvalidStopId() {
        // GIVEN
        String fileName = "invalidStopId.csv";
        File inputFile = getInputFile(fileName);
        String expectedFailureOutputContent = getFailureOutputContent(fileName);
        File failureOutputFile = getOutputFile();

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureOutputContent = getOutputContent(failureOutputFile);
        assertEquals(expectedFailureOutputContent, failureOutputContent);
    }

    @Test
    public void givenTwoInvalidStopId_whenProcess_thenReturnInvalidStopId() {
        // GIVEN
        String fileName = "twoSameKeyInvalidStopIds.csv";
        File inputFile = getInputFile(fileName);
        String expectedFailureOutputContent = getFailureOutputContent(fileName);
        File failureOutputFile = getOutputFile();

        // WHEN
        touchProcessor.processFailure(inputFile, failureOutputFile);

        // THEN
        String failureOutputContent = getOutputContent(failureOutputFile);
        assertEquals(expectedFailureOutputContent, failureOutputContent);
    }

    @Test
    public void givenOneOnTouch_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "oneOnTouch.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoSameKeyOnTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoSameKeyOnTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoDifferentKeyOnTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoDifferentKeyOnTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenOneOffTouch_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "oneOffTouch.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoSameKeyOffTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoSameKeyOffTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoDifferentKeyOffTouches_whenProcess_thenReturnOneIncompleteTrip() {
        // GIVEN
        String fileName = "twoDifferentKeyOffTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenSameStopTouches_whenProcess_thenReturnOneCancelledTrip() {
        // GIVEN
        String fileName = "twoSameStopTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoPairsOfSameStopTouches_whenProcess_thenReturnTwoCancelledTrips() {
        // GIVEN
        String fileName = "twoPairsOfSameStopTouches.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenBetweenAAndB_whenProcess_thenReturnBetweenAAndB() {
        // GIVEN
        String fileName = "betweenAAndB.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenBetweenBAndA_whenProcess_thenReturnBetweenAAndB() {
        // GIVEN
        String fileName = "betweenBAndA.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenBetweenBAndC_whenProcess_thenReturnBetweenBAndC() {
        // GIVEN
        String fileName = "betweenBAndC.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenBetweenCAndB_whenProcess_thenReturnBetweenCAndB() {
        // GIVEN
        String fileName = "betweenCAndB.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenBetweenAAndC_whenProcess_thenReturnBetweenAAndC() {
        // GIVEN
        String fileName = "betweenAAndC.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenBetweenCAndA_whenProcess_thenReturnBetweenCAndA() {
        // GIVEN
        String fileName = "betweenCAndA.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoTrips_whenProcess_thenReturnTwoTrips() {
        // GIVEN
        String fileName = "twoTrips.csv";
        File inputFile = getInputFile(fileName);
        String expectedContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String outputContent = getOutputContent(outputFile);
        assertEquals(expectedContent, outputContent);
    }

    @Test
    public void givenTwoTrips_whenProcessSummary_thenReturnSummary() {
        // GIVEN
        String fileName = "twoTrips.csv";
        File inputFile = getInputFile(fileName);
        String expectedSummaryOutputContent = getSummaryOutputContent(fileName);
        File summaryOutputFile = getOutputFile();

        // WHEN
        touchProcessor.processSummary(inputFile, summaryOutputFile);

        // THEN
        String summaryOutputContent = getOutputContent(summaryOutputFile);
        assertEquals(expectedSummaryOutputContent, summaryOutputContent);
    }

    @Test
    public void givenFoo_whenProcessSummary_thenReturnBar() {
        // GIVEN
        String fileName = "touchDataImproved.csv";
        File inputFile = getInputFile(fileName);
        String expectedOutputContent = getExpectedOutputFileContent(fileName);
        File outputFile = getOutputFile();

        // WHEN
        touchProcessor.process(inputFile, outputFile);

        // THEN
        String summaryOutputContent = getOutputContent(outputFile);
        assertEquals(expectedOutputContent, summaryOutputContent);
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

    private String getExpectedOutputFileContent(String fileName) {
        String directory = "csv/trip";
        return getFileContent(directory, fileName);
    }

    private String getFailureOutputContent(String fileName) {
        String directory = "csv/failure";
        return getFileContent(directory, fileName);
    }

    private String getSummaryOutputContent(String fileName) {
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
    private File getOutputFile() {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);
        tempFile.deleteOnExit();
        return tempFile;
    }

    @SneakyThrows
    private String getOutputContent(File outputFile) {
        return Files.readAllLines(outputFile.toPath()).stream()
            .collect(Collectors.joining(""));
    }
}