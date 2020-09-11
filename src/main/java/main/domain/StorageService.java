package main.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class StorageService {
    private final String rootPath = new File("").getAbsolutePath()
            .concat("/src/main/resources/static/");
    private static final String ABC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom randomGenerator = new SecureRandom();

    private String createRelativePath(String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append("/upload/");
        for(int j =0;j<3;j++) {
            for (int i = 0; i < 5; i++) {
                sb.append(ABC.charAt(randomGenerator.nextInt(ABC.length())));
            }
            sb.append("/");
        }
        sb.append(filename);
        return sb.toString();
    }

    public String store(MultipartFile multipartFile){
        if(!(multipartFile.getOriginalFilename().contains(".jpg") ||
                multipartFile.getOriginalFilename().contains(".png")||
                multipartFile.getOriginalFilename().contains(".jpeg"))){
            return "Недопустимый тип файла";
        } if(multipartFile.getSize()/(1024*1024) > 5024){
            return "Размер файла превышает допустимый размер";
        }
        String relativePath = createRelativePath(multipartFile.getOriginalFilename());
        Path absolutePath = Paths.get(rootPath,relativePath);
        try {
            Files.createDirectories(absolutePath);
            multipartFile.transferTo(new File(absolutePath.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relativePath;
    }

    //что делать с пустыми папками?
    public boolean delete(String filename) {
        boolean result = false;
        if(!filename.startsWith("/upload/")){
            return result;
        }
        try {
            result = Files.deleteIfExists(Paths.get(rootPath, filename));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("No such file exists: " + filename, e);
        } catch (IOException e) {
            throw new RuntimeException("Invalid permissions for file: " + filename, e);
        }
        return result;
    }

    public byte[] getImageByPath(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
    }


    public String storePhoto(MultipartFile photo) {
        if(!(photo.getOriginalFilename().contains(".jpg") || photo.getOriginalFilename().contains(".png") ||
                photo.getOriginalFilename().contains(".jpeg"))){
            return "Недопустимый тип файла";
        }
        if(photo.getSize()/(1024*1024) > 5024){
            return "Размер файла превышает допустимый размер";
        }
        String relativePath = createRelativePath(photo.getOriginalFilename());
        System.out.println(relativePath);
        try {
            Files.createDirectories(Paths.get(rootPath,relativePath));
            BufferedImage originalImage = ImageIO.read(photo.getInputStream());
            BufferedImage scaledBI = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaledBI.createGraphics();
            g.drawImage(originalImage, 0, 0, 36, 36, null);
            g.dispose();

            ImageIO.write(scaledBI, "jpeg", new File(rootPath + relativePath));

        } catch (IOException e) {
             e.printStackTrace();
        }
        return relativePath;

    }

 }
