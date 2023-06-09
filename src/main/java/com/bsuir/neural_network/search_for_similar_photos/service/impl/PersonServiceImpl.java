package com.bsuir.neural_network.search_for_similar_photos.service.impl;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import com.bsuir.neural_network.search_for_similar_photos.enumeration.Role;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.model.Person;
import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import com.bsuir.neural_network.search_for_similar_photos.repository.PersonRepository;
import com.bsuir.neural_network.search_for_similar_photos.service.PersonService;
import com.bsuir.neural_network.search_for_similar_photos.service.UserService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void registration(String name, String surname, String patronymic, String passportSeries, String passportNumber, String dateOfBirth, MultipartFile file, String usernameWithToken) throws UsernameExistException, ParseException, IOException {
        User user = userService.findByUsername(usernameWithToken);
        user.setRole(Role.ROLE_PERSON.name());
        user.setAuthorities(Role.ROLE_PERSON.getAuthorities());
        Person person = savePhoto(usernameWithToken, file);
        person.setName(name);
        person.setSurname(surname);
        person.setPatronymic(patronymic);
        person.setPassportSeries(passportSeries);
        person.setPassportNumber(passportNumber);
        val formatter = new SimpleDateFormat("MM-dd-yyyy", new Locale("Europe/Minsk"));
        val db = formatter.parse(dateOfBirth);
        person.setDateOfBirth(db);
        person.setUser(user);
        personRepository.save(person);
    }

        private Person savePhoto(String username, MultipartFile photo) throws IOException {
        if (photo != null){
            Path userFolder = Paths.get(USER_FOLDER + FORWARD_SLASH + username).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
            }
            String name = generateName();
            Files.deleteIfExists(Paths.get(userFolder + FORWARD_SLASH + name +  DOT + JPG_EXTENSION));
            Files.copy(photo.getInputStream(), userFolder.resolve(name + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            Person person = new Person();
            person.setImagePassport(setLogoImageUrl(username, name));
            return person;
        }
        return new Person();
    }

    private String setLogoImageUrl(String username, String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
                path(USER_PATH + FORWARD_SLASH + username + FORWARD_SLASH + name + DOT + JPG_EXTENSION).toUriString();
    }

    private String generateName() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

}