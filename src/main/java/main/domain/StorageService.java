package main.domain;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class StorageService {

    private final String rootPath = new File("").getAbsolutePath()
            .concat("/src/main/resources");
    private Random random = new Random();

    public String store(MultipartFile file){
        if(!file.getOriginalFilename().contains(".jpg") || !file.getOriginalFilename().contains(".png")){
            return "Недопустимый тип файла";
        } if(file.getSize()/(1024*1024) > 5024){
            return "Размер файла превышает допустимый размер";
        }
        String prefix = "/static";

        String absolutePathToFolder =
                "/img/upload/" + generatePathPart() + "/" + generatePathPart() + "/";
        new File(rootPath + prefix + absolutePathToFolder).mkdirs();
        String path = absolutePathToFolder + file.getOriginalFilename();
        try {
            Files.copy(file.getInputStream(), Paths.get(rootPath  + prefix + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    private String generatePathPart(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public boolean delete(String filename) {
        boolean result = false;
        try {
            result = Files.deleteIfExists(Path.of(filename));
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
        if(!photo.getOriginalFilename().contains(".jpg") && !photo.getOriginalFilename().contains(".png")){
            return "Недопустимый тип файла";
        }
        if(photo.getSize()/(1024*1024) > 5024){
            return "Размер файла превышает допустимый размер";
        }
        String prefix = "/static";
        String absolutePathToFolder =
                "/img/upload/" + generatePathPart() + "/" + generatePathPart() + "/";
        new File(rootPath + prefix + absolutePathToFolder).mkdirs();
        String path = absolutePathToFolder + photo.getOriginalFilename();

        try {
            BufferedImage originalImage = ImageIO.read(photo.getInputStream());
            BufferedImage scaledBI = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaledBI.createGraphics();
            g.drawImage(originalImage, 0, 0, 36, 36, null);
            g.dispose();

            ImageIO.write(scaledBI, "jpeg", new File(rootPath  + prefix + path));

        } catch (IOException e) {
             e.printStackTrace();
        }
        return path;

    }

 }
