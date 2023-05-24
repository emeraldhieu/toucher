package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A touch processor.
 */
@RequiredArgsConstructor
@Slf4j
public class TouchProcessor {

    // Assume the charset is UTF-8 which covers most cases
    private String charset = "UTF-8";

    // Assume the CSV delimiter is comma
    private CsvDelimiter csvDelimiter = CsvDelimiter.COMMA;

    /**
     * A map of summary lines grouped by keys. Key is composed of date, companyId, and busId.
     */
    private final Map<Key, SummaryLine> summaryLinesByKey = new HashMap<>();

    void process(File inputFile, File outputFile) {
        try {
            File failureTempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            File summaryTempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            process(inputFile, outputFile, failureTempFile, summaryTempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void processFailure(File inputFile, File failureFile) {
        try {
            File outputTempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            File summaryTempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            process(inputFile, outputTempFile, failureFile, summaryTempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void processSummary(File inputFile, File summaryFile) {
        try {
            File outputTempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            File failureTempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            process(inputFile, outputTempFile, failureTempFile, summaryFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void process(InputStream inputStream, OutputStream tripOutputStream, OutputStream failureOutputStream,
                        OutputStream summaryOutputStream) {
        CsvMapper inputMapper = new CsvMapper();
        inputMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        CsvSchema inputSchema = inputMapper
            .schemaFor(InputLine.class)
            .withColumnSeparator(csvDelimiter.getKeyword())
            .withHeader();

        ObjectWriter outputObjectWriter = getObjectWriter(ResultLine.class);
        ObjectWriter summaryObjectWriter = getObjectWriter(SummaryLine.class);

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
             SequenceWriter successSequenceWriter = outputObjectWriter.writeValues(tripOutputStream);
             SequenceWriter failureSequenceWriter = outputObjectWriter.writeValues(failureOutputStream);
             SequenceWriter summarySequenceWriter = summaryObjectWriter.writeValues(summaryOutputStream)) {

            MappingIterator<InputLine> csvIterator = inputMapper.readerFor(InputLine.class)
                .with(inputSchema)
                .readValues(inputStreamReader);

            if (!csvIterator.hasNext()) {
                throw new TouchProcessorException("CSV doesn't have any row");
            }

            /**
             * A pair of lines that represents a trip.
             */
            List<InputLine> linePair = new ArrayList<>();

            while (csvIterator.hasNext()) {
                try {
                    InputLine rawLine = csvIterator.next();
                    TouchType touchType = getTouchType(failureSequenceWriter, rawLine);
                    InputLine line = rawLine.toBuilder()
                        .touchTypeEnum(touchType)
                        .build();
                    validateStopId(failureSequenceWriter, line);

                    linePair.add(line);

                    if (linePair.size() == 1
                        && linePair.get(0).getTouchTypeEnum() == TouchType.OFF
                    ) {
                        ResultLine resultLine = writeIncompleteTrip(successSequenceWriter, linePair);
                        updateSummaryLine(resultLine);
                    }

                    if (linePair.size() == 2
                        && linePair.get(0).getTouchTypeEnum() == linePair.get(1).getTouchTypeEnum()
                    ) {
                        ResultLine resultLine = writeIncompleteTrip(successSequenceWriter, linePair);
                        updateSummaryLine(resultLine);
                    }

                    if (linePair.size() == 2) {
                        if (linePair.get(0).getStopId().equals(linePair.get(1).getStopId())) {
                            ResultLine resultLine = writeTrip(successSequenceWriter, linePair, TripStatus.CANCELLED);
                            updateSummaryLine(resultLine);
                        } else {
                            ResultLine resultLine = writeTrip(successSequenceWriter, linePair, TripStatus.COMPLETED);
                            updateSummaryLine(resultLine);
                        }
                    }

                } catch (TouchProcessorException exception) {
                    /*
                     * Continue next line. Feel free to log into the console if needed.
                     */
                }
            }

            if (linePair.size() == 1) {
                ResultLine resultLine = writeIncompleteTrip(successSequenceWriter, linePair);
                updateSummaryLine(resultLine);
            }

            writeSummaryLines(summarySequenceWriter);

        } catch (IOException e) {
            throw new TouchProcessorException("Can't process the file", e);
        }
    }

    public void process(File inputFile, File tripFile, File failureOutputFile, File summaryOutputFile) {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             FileOutputStream tripOutputStream = new FileOutputStream(tripFile);
             FileOutputStream failureOutputStream = new FileOutputStream(failureOutputFile);
             FileOutputStream summaryOutputStream = new FileOutputStream(summaryOutputFile)) {
            process(fileInputStream, tripOutputStream, failureOutputStream, summaryOutputStream);
        } catch (IOException e) {
            throw new TouchProcessorException("Can't process the file", e);
        }
    }

    private static ObjectWriter getObjectWriter(Class clazz) {
        CsvMapper summaryMapper = new CsvMapper();
        CsvSchema summarySchema = summaryMapper
            .schemaFor(clazz)
            .withHeader();
        ObjectWriter summaryObjectWriter = summaryMapper
            /**
             * Always double-quote both headers and values
             * because the library uses heuristics to add double quotes to long-enough strings.
             */
            .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
            .writer(summarySchema);
        return summaryObjectWriter;
    }

    private void writeSummaryLines(SequenceWriter summarySequenceWriter) {
        List<Key> sortedKeys = getSortedKeyOfSummaryLines();
        sortedKeys.forEach(key -> {
            SummaryLine summaryLine = summaryLinesByKey.get(key);
            try {
                summarySequenceWriter.write(summaryLine);
            } catch (IOException e) {
                throw new TouchProcessorException("Something wrong", e);
            }
        });
    }

    private List<Key> getSortedKeyOfSummaryLines() {
        Comparator<Key> keyComparator = Comparator.comparing(Key::getDate)
            .thenComparing(Key::getCompanyId)
            .thenComparing(Key::getBusId);
        List<Key> sortedSummaryDates = summaryLinesByKey.keySet().stream()
            .sorted(keyComparator)
            .collect(Collectors.toList());
        return sortedSummaryDates;
    }

    private void updateSummaryLine(ResultLine resultLine) {
        Key key = resultLine.getKey();
        if (summaryLinesByKey.containsKey(key)) {
            SummaryLine summaryLine = summaryLinesByKey.get(key);
            SummaryLine updatedSummaryLine = summaryLine.toBuilder()
                .completeTripCount(resultLine.getTripStatus() == TripStatus.COMPLETED
                    ? summaryLine.getCompleteTripCount() + 1
                    : summaryLine.getCompleteTripCount())
                .incompleteTripCount(resultLine.getTripStatus() == TripStatus.INCOMPLETE
                    ? summaryLine.getIncompleteTripCount() + 1
                    : summaryLine.getIncompleteTripCount())
                .cancelledTripCount(resultLine.getTripStatus() == TripStatus.CANCELLED
                    ? summaryLine.getCancelledTripCount() + 1
                    : summaryLine.getCancelledTripCount())
                .totalCharges(summaryLine.getTotalCharges() + resultLine.getChargeAmount())
                .build();
            summaryLinesByKey.put(key, updatedSummaryLine);
        } else {
            SummaryLine summaryLine = SummaryLine.builder()
                .date(resultLine.getSummaryDate())
                .companyId(resultLine.getCompanyId())
                .busId(resultLine.getBusId())
                .completeTripCount(resultLine.getTripStatus() == TripStatus.COMPLETED
                    ? 1
                    : 0)
                .incompleteTripCount(resultLine.getTripStatus() == TripStatus.INCOMPLETE
                    ? 1
                    : 0)
                .cancelledTripCount(resultLine.getTripStatus() == TripStatus.CANCELLED
                    ? 1
                    : 0)
                .totalCharges(resultLine.getChargeAmount())
                .build();
            summaryLinesByKey.put(key, summaryLine);
        }
    }

    @SneakyThrows
    private ResultLine writeTrip(SequenceWriter sequenceWriter, List<InputLine> linePair,
                                 TripStatus tripStatus) {
        String started = linePair.get(0).getDateTimeUtc();
        String finished = linePair.get(1).getDateTimeUtc();
        String fromStopId = linePair.get(0).getStopId();
        String toStopId = linePair.get(1).getStopId();
        double chargeAmount = StopCharge.getCharge(fromStopId, toStopId);
        String companyId = linePair.get(0).getCompanyId();
        String busId = linePair.get(0).getBusId();
        String hashedPan = HashedPan.from(linePair.get(0).getPan(), linePair.get(1).getPan());
        ResultLine resultLine = ResultLine.builder()
            .started(started)
            .finished(finished)
            .fromStopId(fromStopId)
            .toStopId(toStopId)
            .chargeAmount(chargeAmount)
            .companyId(companyId)
            .busId(busId)
            .hashedPan(hashedPan)
            .tripStatus(tripStatus)
            .build();
        sequenceWriter.write(resultLine);

        linePair.clear(); // clear only if succeeded

        return resultLine;
    }

    private ResultLine writeIncompleteTrip(SequenceWriter sequenceWriter, List<InputLine> linePair) throws IOException {
        InputLine firstLine = linePair.get(0);
        String started = firstLine.getTouchTypeEnum() == TouchType.ON
            ? firstLine.getDateTimeUtc()
            : null;
        String finished = firstLine.getTouchTypeEnum() == TouchType.ON
            ? null
            : firstLine.getDateTimeUtc();
        String fromStopId = firstLine.getTouchTypeEnum() == TouchType.ON
            ? firstLine.getStopId()
            : "";
        String toStopId = firstLine.getTouchTypeEnum() == TouchType.ON
            ? ""
            : firstLine.getStopId();
        double chargeAmount = StopCharge.getCharge(fromStopId, toStopId);
        String companyId = firstLine.getCompanyId();
        String busId = firstLine.getBusId();
        String hashedPan = firstLine.getTouchTypeEnum() == TouchType.ON
            ? HashedPan.from(firstLine.getPan(), "")
            : HashedPan.from("", firstLine.getPan());
        ResultLine resultLine = ResultLine.builder()
            .started(started)
            .finished(finished)
            .fromStopId(fromStopId.equals("") ? null : fromStopId)
            .toStopId(toStopId.equals("") ? null : toStopId)
            .chargeAmount(chargeAmount)
            .companyId(companyId)
            .busId(busId)
            .hashedPan(hashedPan)
            .tripStatus(TripStatus.INCOMPLETE)
            .build();
        sequenceWriter.write(resultLine);

        linePair.remove(firstLine);

        return resultLine;
    }

    @SneakyThrows
    private TouchType getTouchType(SequenceWriter failureSequenceWriter, InputLine line) {
        try {
            return TouchType.forKeyword(line.getTouchType());
        } catch (IllegalArgumentException e) {
            ResultLine resultLine = ResultLine.builder()
                // Can't set started, finished, fromStopId, and toStopId because the touchType is unknown
                .companyId(line.getCompanyId())
                .busId(line.getBusId())
                // Assume leaving the other PAN empty
                .hashedPan(HashedPan.from(line.getPan(), ""))
                .tripStatus(TripStatus.INVALID_TOUCH_TYPE)
                .build();
            failureSequenceWriter.write(resultLine);
            throw new TouchProcessorException(TripStatus.INVALID_TOUCH_TYPE.getMessage());
        }
    }

    @SneakyThrows
    private String validateStopId(SequenceWriter failureSequenceWriter, InputLine line) {
        try {
            StopCharge.validateStopId(line.getStopId());
            return line.getStopId();
        } catch (IllegalArgumentException e) {
            TouchType touchType = line.getTouchTypeEnum();
            ResultLine resultLine = ResultLine.builder()
                .started(touchType == TouchType.ON ? line.getDateTimeUtc() : null)
                .finished(touchType == TouchType.OFF ? line.getDateTimeUtc() : null)
                .fromStopId(touchType == TouchType.ON ? line.getStopId() : null)
                .toStopId(touchType == TouchType.OFF ? line.getStopId() : null)
                .companyId(line.getCompanyId())
                .busId(line.getBusId())
                .hashedPan(HashedPan.from(line.getPan(), ""))
                .tripStatus(TripStatus.INVALID_STOP_ID)
                .build();
            failureSequenceWriter.write(resultLine);
            throw new TouchProcessorException(TripStatus.INVALID_STOP_ID.getMessage());
        }
    }
}
