package com.bsuir.neural_network.search_for_similar_photos.service.impl;

import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.model.Image;
import com.bsuir.neural_network.search_for_similar_photos.model.Keyword;
import com.bsuir.neural_network.search_for_similar_photos.repository.ImageRepository;
import com.bsuir.neural_network.search_for_similar_photos.repository.KeywordRepository;
import com.bsuir.neural_network.search_for_similar_photos.service.ImageService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final KeywordRepository keywordRepository;


    @Override
    public void upload(String keywords, MultipartFile file) throws IOException {
        Image image = new Image();
        String[] words = keywords.split("[\\s,]+");
        for(String word: words) {
            Keyword keyword = keywordRepository.findByWord(word.toUpperCase()).orElse(new Keyword());
            keyword.setWord(word.toUpperCase());
            image.addKeyword(keyword);
        }
        imageRepository.save(saveImage(image, file));
    }

    @Override
    public List<ImageAnswerDTO> getAll() {
        return imageRepository.findAll().stream().map(this::createImageAnswerDTO).collect(Collectors.toList());
    }

    @Override
    public List<ImageAnswerDTO> similarImageSearch(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        Mat img1 = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);
        return imageRepository.findAll().stream().filter(image -> {
            try {
                byte[] imageBytes = Files.readAllBytes(Paths.get(USER_FOLDER + FORWARD_SLASH + image.getName() + DOT + JPG_EXTENSION));
                Mat img2 =  Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);
                // Convert to grayscale
                Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY);

                // Match template
                Mat result = new Mat();
                Imgproc.matchTemplate(img1, img2, result, Imgproc.TM_CCOEFF_NORMED);

                // Find best match
                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                double matchScore = mmr.maxVal;
                int matchX = (int)mmr.maxLoc.x;
                int matchY = (int)mmr.maxLoc.y;
                if (matchScore > 0) return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }).map(this::createImageAnswerDTO).collect(Collectors.toList());
    }

    private ImageAnswerDTO createImageAnswerDTO(Image image) {
        ImageAnswerDTO imageAnswerDTO = new ImageAnswerDTO();
        imageAnswerDTO.setId(image.getId());
        imageAnswerDTO.setUrl(image.getUrl());
        imageAnswerDTO.getKeywords().addAll(image.getKeywords().stream().map(Keyword::getWord).collect(Collectors.toSet()));
        return imageAnswerDTO;
    }

    private Image saveImage(Image image, MultipartFile file) throws IOException {
        if (file != null){
            Path userFolder = Paths.get(USER_FOLDER).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
            }
            String name = generateName();
            while (imageRepository.findByName(name).isPresent()){
                name = generateName();
            }
            Files.deleteIfExists(Paths.get(userFolder + FORWARD_SLASH + name +  DOT + JPG_EXTENSION));
            Files.copy(file.getInputStream(), userFolder.resolve(name + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            image.setUrl(setLogoImageUrl(name));
            image.setName(name);
            return image;
        }
        return image;
    }
//
//    private String setLogoImageUrl(String name) {
//        return "http://localhost:8080" + USER_IMAGE_PATH + FORWARD_SLASH + name + DOT + JPG_EXTENSION;
//    }

    private String setLogoImageUrl(String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
                path(USER_IMAGE_PATH + FORWARD_SLASH + name + DOT + JPG_EXTENSION).toUriString();
    }

    private String generateName() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
