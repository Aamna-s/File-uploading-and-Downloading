package com.example.image.Image_processing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.nio.file.Path;
@Service
public class ImageService {

    String folderPath =System.getProperty("Image_FOLDER_PATH") ;


    private final ImageRepository imageRepository;
    private final ApiService apiService;

    @Autowired
    public ImageService(ImageRepository imageRepository , ApiService apiService) {
        this.imageRepository = imageRepository;
        this.apiService=apiService;
    }


    public String upload(MultipartFile file) throws IOException {

        String filePath =  folderPath+'\\'+file.getOriginalFilename();


        Image img = Image.builder()
                .url(filePath)
                .build();

        imageRepository.save(img);
        saveImageToStorage(folderPath,file);


        if (img != null) {
            return "Upload successfully";
        }
        return "Upload failed";
    }
    public String saveImageToStorage(String uploadDirectory, MultipartFile imageFile) throws IOException {

        String uniqueFileName = imageFile.getOriginalFilename();
        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        apiService.callAnotherApi("http://localhost:5000/upload",uploadDirectory+'\\'+uniqueFileName);
        return uniqueFileName;
    }

    public byte[] getImage(String folderPath, String fileName) throws IOException {

        String fullPath = folderPath + "YOLO_Chef_"+fileName ;
        File imageFile = new File(fullPath);

        if (!imageFile.exists()) {
            throw new IOException("File not found at path: " + fullPath);
        }

        return Files.readAllBytes(imageFile.toPath());
    }

}
