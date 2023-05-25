package com.emeraldhieu.toucher.rest;

import com.emeraldhieu.toucher.touch.RouteProvider;
import com.emeraldhieu.toucher.touch.TouchProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ToucherService {

    private final TouchProcessor touchProcessor;

    public ResponseEntity<StreamingResponseBody> process(MultipartFile multipartFile) {
        StreamingResponseBody body = outputStream -> {
            try (
                InputStream inputStream = multipartFile.getInputStream();
                PipedInputStream pipedTripInputStream = new PipedInputStream();
                PipedOutputStream pipedTripOutputStream = new PipedOutputStream(pipedTripInputStream);
                PipedInputStream pipedFailureInputStream = new PipedInputStream();
                PipedOutputStream pipedFailureOutputStream = new PipedOutputStream(pipedFailureInputStream);
                PipedInputStream pipedSummaryInputStream = new PipedInputStream();
                PipedOutputStream pipedSummaryOutputStream = new PipedOutputStream(pipedSummaryInputStream);
                ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            ) {
                CompletableFuture.runAsync(() -> {
                    touchProcessor.process(inputStream, pipedTripOutputStream, pipedFailureOutputStream, pipedSummaryOutputStream);
                });

                CompletableFuture<Void> writeZipFuture = CompletableFuture.runAsync(() -> {
                    try {
                        writeToZipOutputStream(pipedTripInputStream, zipOutputStream, "trip.csv");
                        writeToZipOutputStream(pipedFailureInputStream, zipOutputStream, "unprocessableTouchData.csv");
                        writeToZipOutputStream(pipedSummaryInputStream, zipOutputStream, "tripSummary.csv");
                    } catch (IOException e) {
                        throw new ToucherException("Failed to write to ZIP", e);
                    }
                });

                /**
                 * There are two reasons:
                 * + Avoid inputStream from being closed before all processes are done
                 * + Wait for zip task to finish. Otherwise, ZipOutputStream will be closed at the end of try-with-resource is reached
                 */
                writeZipFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new ToucherException("Can't process CSV file", e);
            }
        };

        return ResponseEntity.status(HttpStatus.OK)
            .headers(httpHeaders -> {
                httpHeaders.setContentType(new MediaType("application", "zip", StandardCharsets.UTF_8));
                String zipFileName = "result.zip";
                httpHeaders.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(zipFileName)
                    .build());
            })
            .body(body);
    }

    private void writeToZipOutputStream(InputStream inputStream, ZipOutputStream zipOutputStream, String fileName) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        inputStream.transferTo(zipOutputStream);
        zipOutputStream.closeEntry();
    }
}
