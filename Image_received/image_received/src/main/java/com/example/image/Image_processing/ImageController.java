package com.example.image.Image_processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
public class ImageController {
    @Value("${file.upload.dir:src/main/resources/static/images/}")
    String folderPath ;
    ImageService imageService;
    @Autowired
    public ImageController(ImageService imageService){
        this.imageService=imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") String encodedImage,@RequestParam("imgName") String imgName) {

        try {

            String cleanImg =encodedImage.replaceAll("\\s+", "");

            System.out.println(cleanImg);
            byte[] decodedBytes = Base64.getDecoder().decode(cleanImg);
            MultipartFile img= toMultipartFile(decodedBytes,"Received_"+imgName,"File");
            String fileName = imageService.upload(img);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + fileName);
        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }
    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
        try {

            byte[] imageData = imageService.getImage(folderPath, fileName);


            if (imageData == null || imageData.length == 0) {
                throw new IOException("Image data is empty or null");
            }

            String base64Image = Base64.getEncoder().encodeToString(imageData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(base64Image);
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

    public static MultipartFile toMultipartFile(byte[] bytes, String fileName, String contentType) {

        return new ByteArrayMultipartFile(bytes,fileName,contentType);
    }
}
