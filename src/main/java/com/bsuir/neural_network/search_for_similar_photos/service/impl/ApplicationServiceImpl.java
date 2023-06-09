package com.bsuir.neural_network.search_for_similar_photos.service.impl;

import com.bsuir.neural_network.search_for_similar_photos.enumeration.Status;
import com.bsuir.neural_network.search_for_similar_photos.model.Application;
import com.bsuir.neural_network.search_for_similar_photos.model.Person;
import com.bsuir.neural_network.search_for_similar_photos.model.SampleApplication;
import com.bsuir.neural_network.search_for_similar_photos.repository.ApplicationRepository;
import com.bsuir.neural_network.search_for_similar_photos.repository.PersonRepository;
import com.bsuir.neural_network.search_for_similar_photos.repository.SampleApplicationRepository;
import com.bsuir.neural_network.search_for_similar_photos.service.ApplicationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.*;

@Service
@AllArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PersonRepository personRepository;
    private final SampleApplicationRepository sampleApplicationRepository;

    @Override
    @Transactional
    public void registration(Long id, String usernameWithToken) throws IOException {
        Person person = personRepository.findByUserUsername(usernameWithToken).get();
        SampleApplication sa = sampleApplicationRepository.findById(id).get();
        String name = generateName();

        Path file = Paths.get(USER_FOLDER + FORWARD_SLASH + sa.getName() + DOT + DOCX_EXTENSION);
        try (FileInputStream fileInputStream = new FileInputStream(file.toAbsolutePath().toFile())) {
            XWPFDocument doc = new XWPFDocument(fileInputStream);

            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph para : cell.getParagraphs()) {
                            for (XWPFRun run : para.getRuns()) {
                                CTText ctText = run.getCTR().getTArray(0);
                                if (ctText.getStringValue().equals("Иван")) {
                                    String replacedText = ctText.getStringValue().replace("Иван", person.getName());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("Иванов")) {
                                    String replacedText = ctText.getStringValue().replace("Иванов", person.getSurname());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("Иванович")) {
                                    String replacedText = ctText.getStringValue().replace("Иванович", person.getPatronymic());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("01.01.2000")) {
                                    String replacedText = ctText.getStringValue().replace("01.01.2000", person.getDateOfBirth().toString());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("КВ")) {
                                    String replacedText = ctText.getStringValue().replace("КВ", person.getPassportSeries());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("1111111")) {
                                    String replacedText = ctText.getStringValue().replace("1111111", person.getPassportNumber());
                                    ctText.setStringValue(replacedText);
                                }
                            }
                        }
                    }
                }
            }
            Path userFolder = Paths.get(USER_FOLDER + FORWARD_SLASH + person.getUser().getUsername() + FORWARD_SLASH + sa.getName()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(userFolder + FORWARD_SLASH + name + DOT + DOCX_EXTENSION)) {
                doc.write(fileOutputStream);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Application application = new Application();
        application.setNumber(generateName());
        application.setDate(new Date());
        application.setFile(setLogoDOCXUrl(person.getUser().getUsername(), name));
        application.setStatus(Status.INACTIVITY.name());
        person.addApp(application);
        personRepository.save(person);
    }

    private String generateName() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String setLogoDOCXUrl(String username, String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
                path(APP_PATH + FORWARD_SLASH + username + FORWARD_SLASH + name + DOT + DOCX_EXTENSION).toUriString();
    }

}
