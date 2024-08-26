package com.example.image.Image_processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
public class ImageController {

    String folderPath = System.getProperty("Image_FOLDER_PATH");
    ImageService imageService;
    ApiService apiService;
    @Autowired
    public ImageController(ImageService imageService, ApiService apiService){
        this.imageService=imageService;
        this.apiService =apiService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String fileName = imageService.upload(file);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
        return null;
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadImage(@RequestParam("fileName") String fileName) {
        try {

            byte[] imageData = imageService.getImage(folderPath, fileName);


            if (imageData == null || imageData.length == 0) {
                throw new IOException("Image data is empty or null");
            }


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.valueOf("image/png"))
                    .body(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the file");
        }
    }


}
