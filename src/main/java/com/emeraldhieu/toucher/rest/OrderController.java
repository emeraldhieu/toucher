package com.emeraldhieu.toucher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final ToucherService toucherService;

    @PostMapping(value = "/views/{viewId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TouchResponse> createOrder(@RequestParam("file") MultipartFile file) {
        // TODO Implement later
        toucherService.process();
        TouchResponse touchRepsonse = null;
        return ResponseEntity.ok(touchRepsonse);
    }
}