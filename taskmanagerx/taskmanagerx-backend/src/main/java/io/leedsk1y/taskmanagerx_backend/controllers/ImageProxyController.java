package io.leedsk1y.taskmanagerx_backend.controllers;

import io.leedsk1y.taskmanagerx_backend.services.ImageProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy")
public class ImageProxyController {
    private final ImageProxyService imageService;

    public ImageProxyController(ImageProxyService imageService) {
        this.imageService = imageService;
    }

    /**
     * Fetches an image from a given URL by proxying the request through the backend.
     * Delegates the actual image fetching to the ImageService.
     *
     * @param url The URL of the image to fetch.
     * @return A ResponseEntity containing the image or an error response if fetching fails.
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> fetchGoogleImage(@RequestParam String url) {
        return imageService.fetchImage(url);
    }
}
