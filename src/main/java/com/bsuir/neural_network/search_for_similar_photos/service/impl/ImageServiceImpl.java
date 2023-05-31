package com.bsuir.neural_network.search_for_similar_photos.service.impl;

import com.bsuir.neural_network.search_for_similar_photos.dto.answer.HistoryAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.enumeration.SearchScore;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.model.History;
import com.bsuir.neural_network.search_for_similar_photos.model.Image;
import com.bsuir.neural_network.search_for_similar_photos.model.Keyword;
import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import com.bsuir.neural_network.search_for_similar_photos.repository.ImageRepository;
import com.bsuir.neural_network.search_for_similar_photos.repository.KeywordRepository;
import com.bsuir.neural_network.search_for_similar_photos.repository.UserRepository;
import com.bsuir.neural_network.search_for_similar_photos.service.ImageService;
import com.bsuir.neural_network.search_for_similar_photos.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final KeywordRepository keywordRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private Map<Integer, Set<Image>> imagesWithOpenCV = new HashMap<>();

    @Override
    public void upload(String keywords, MultipartFile file) throws IOException {
        Image image = new Image();
        splitStringIntoWords(keywords).forEach(image::addKeyword);
        imageRepository.save(createObjectImage(image, file));
    }

    private Set<Keyword> splitStringIntoWords(String keywords) {
        Set<Keyword> keyws = new HashSet<>();
        String[] words = keywords.split("[\\s,]+");
        for(String word: words) {
            if(keywordRepository.findByWord(word).isPresent()){
                Keyword keyword = keywordRepository.findByWord(word.toUpperCase()).get();
                keyws.add(keyword);
            } else {
                Keyword keyword = new Keyword();
                keyword.setWord(word.toUpperCase());
                keyws.add(keyword);
            }
        }
        return keyws;
    }

    @Override
    public List<ImageAnswerDTO> getAll() {
        return imageRepository.findAll().stream().map(this::createImageAnswerDTO).collect(Collectors.toList());
    }

    @Override
    public List<ImageAnswerDTO> similarImageSearch(String searchScore, String keywords, MultipartFile file, String usernameWithToken) throws IOException, UsernameExistException {
        createHistory(keywords, file, usernameWithToken);
        if (userRepository.findAll().isEmpty()){
            upload(keywords, file);
            return new ArrayList<>();
        } else {
            double value = getValueForImageEvaluation(searchScore);
            compareImage(file, value);
            Image image = provideTraining(keywords, file);
            Set<Image> findSimilarImages = imagesWithOpenCV.get(3);
            List<ImageAnswerDTO> imageAnswerDTOs = findSimilarImages.stream().map(this::createImageAnswerDTO).collect(Collectors.toList());
            imageRepository.save(image);
            return imageAnswerDTOs;
        }
    }

    @Override
    public List<ImageAnswerDTO> getAllImageForUser(String usernameWithToken) throws UsernameExistException {
        return userService.findByUsername(usernameWithToken).getImages()
                .stream().map(this::createImageAnswerDTO).collect(Collectors.toList());
    }

    @Override
    public void imageSaveIForUser(Long id, String usernameWithToken) throws UsernameExistException {
        User user = userService.findByUsername(usernameWithToken);
        Image image = imageRepository.findById(id).get();
        user.addImage(image);
    }

    @Override
    public List<HistoryAnswerDTO> getAllHistoryForUser(String usernameWithToken) throws UsernameExistException {
        return userService.findByUsername(usernameWithToken).getHistories()
                .stream().map(this::createHistoryAnswerDTO).collect(Collectors.toList());
    }

    @Override
    public List<ImageAnswerDTO> findImages(String str){
        String[] words = str.split("[\\s,]+");
        List<Keyword> keywords = new ArrayList<>();
        Arrays.stream(words).forEach(keyword -> {
            Keyword kw = keywordRepository.findByWord(keyword.toUpperCase()).orElse(null);
            if (kw != null) keywords.add(kw);
        });
        List<Image> images = keywords.stream().flatMap(keyword ->
                imageRepository
                        .findByKeywords(keyword).get().stream())
                .toList();
        return images.stream().map(this::createImageAnswerDTO).collect(Collectors.toList());
    }

    private HistoryAnswerDTO createHistoryAnswerDTO(History history){
        HistoryAnswerDTO historyAnswerDTO = new HistoryAnswerDTO();
        historyAnswerDTO.setId(history.getId());
        historyAnswerDTO.setUrl(history.getUrl());
        historyAnswerDTO.setWords(history.getWords());
        return historyAnswerDTO;
    }

    private void createHistory(String keywords, MultipartFile file, String usernameWithToken) throws IOException, UsernameExistException {
        History history = new History();
        String name = createObjectImageForUser(file, usernameWithToken);
        history.setUrl(setLogoImageUrlForUser(name, usernameWithToken));
        String[] words = keywords.split("[\\s,]+");
        history.setWords(words);
        User user = userService.findByUsername(usernameWithToken);
        user.addHistory(history);
        userRepository.save(user);
    }

    private void compareImage(MultipartFile file, double value) throws IOException {
        Set<Image> identicalImages = new HashSet<>();
        Set<Image> imagesForLearning = new HashSet<>();
        Set<Image> imagesToSearch = new HashSet<>();
        byte[] bytes = file.getBytes();
        Mat img1 = Imgcodecs.imdecode(
                new MatOfByte(bytes),
                Imgcodecs.IMREAD_UNCHANGED);
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY);
        imageRepository.findAll().parallelStream().forEach(image -> {
            try {
                if (compareTwoImages(img1, image) == 1) identicalImages.add(image);
                if (compareTwoImages(img1, image) >= 0.0) imagesForLearning.add(image);
                if (compareTwoImages(img1, image) >= value) imagesToSearch.add(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        imagesWithOpenCV.put(1, identicalImages);
        imagesWithOpenCV.put(2, imagesForLearning);
        imagesWithOpenCV.put(3, imagesToSearch);

    }

    private double compareTwoImages(Mat img1, Image image) throws IOException{
        byte[] imageBytes = Files.readAllBytes(Paths.get(USER_FOLDER + FORWARD_SLASH + image.getName() + DOT + JPG_EXTENSION));
        Mat img2 =  Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);
        // Convert to grayscale
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY);
        // Check if images have the same size. If not, resize both images
        if (img1.size().height != img2.size().height || img1.size().width != img2.size().width) {
            Size newSize = new Size(img1.size().width, img1.size().height);
            Imgproc.resize(img1, img1, newSize);
            Imgproc.resize(img2, img2, newSize);
        }
        // Match template
        Mat result = new Mat();
        Imgproc.matchTemplate(img1, img2, result, Imgproc.TM_CCOEFF_NORMED);
        // Find best match
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        return mmr.maxVal;
    }

    private Image provideTraining(String keywords, MultipartFile file) throws IOException {
        Set<Keyword> keywordSet = splitStringIntoWords(keywords);
        Set<Image> images = imagesWithOpenCV.get(2);
        images = images.stream().map(image -> {
                    Set<Keyword> ks = Stream.of(image.getKeywords(), keywordSet)
                            .flatMap(Set::stream).collect(Collectors.toSet());
                    image.setKeywords(new HashSet<>());
                    image.getKeywords().addAll(ks);
                    return image;
                }).collect(Collectors.toSet());
        images = images.stream().map(imageRepository::save).collect(Collectors.toSet());
        if(imagesWithOpenCV.get(1).isEmpty()){
            Image image = createObjectImage(new Image(), file);
            splitStringIntoWords(keywords).forEach(image::addKeyword);
            images.forEach(im-> {
                im.getKeywords().forEach(keyword -> {
                    Set<Keyword> kw = im.getKeywords().stream().filter(k -> !k.equals(keyword)).collect(Collectors.toSet());
                    image.getKeywords().addAll(kw);
                });
            });
            return image;
        } else {
            return imagesWithOpenCV.get(1).iterator().next();
        }
    }



    private double getValueForImageEvaluation(String searchScore) {
        SearchScore score = SearchScore.valueOf(searchScore);
        return switch (score.getValue()) {
            case 1 -> -0.8;
            case 2 -> -0.6;
            case 3 -> -0.4;
            case 4 -> -0.2;
            case 5 -> 0.0;
            case 6 -> 0.2;
            case 7 -> 0.4;
            case 8 -> 0.6;
            case 9 -> 0.8;
            case 10 -> 0.9;
            default -> -1;
        };
    }

    private ImageAnswerDTO createImageAnswerDTO(Image image) {
        ImageAnswerDTO imageAnswerDTO = new ImageAnswerDTO();
        imageAnswerDTO.setId(image.getId());
        imageAnswerDTO.setUrl(image.getUrl());
        imageAnswerDTO.getKeywords().addAll(image.getKeywords().stream().map(Keyword::getWord).collect(Collectors.toSet()));
        return imageAnswerDTO;
    }

    private Image createObjectImage(Image image, MultipartFile file) throws IOException {
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

    private String createObjectImageForUser(MultipartFile file, String username) throws IOException {
        if (file != null){
            Path userFolder = Paths.get(USER_FOLDER + FORWARD_SLASH + username).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
            }
            String name = generateName();
            while (imageRepository.findByName(name).isPresent()){
                name = generateName();
            }
            Files.deleteIfExists(Paths.get(userFolder + FORWARD_SLASH + name +  DOT + JPG_EXTENSION));
            Files.copy(file.getInputStream(), userFolder.resolve(name + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            return name;
        }
        return "";
    }

    private String setLogoImageUrl(String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
                path(IMAGE_PATH + FORWARD_SLASH + name + DOT + JPG_EXTENSION).toUriString();
    }

    private String setLogoImageUrlForUser(String name, String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
                path(IMAGE_PATH + FORWARD_SLASH + USER_PATH + FORWARD_SLASH + username + FORWARD_SLASH + name + DOT + JPG_EXTENSION).toUriString();
    }

    private String generateName() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
