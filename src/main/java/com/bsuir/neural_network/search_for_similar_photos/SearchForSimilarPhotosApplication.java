package com.bsuir.neural_network.search_for_similar_photos;

import com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant;
import com.bsuir.neural_network.search_for_similar_photos.model.SampleApplication;
import com.bsuir.neural_network.search_for_similar_photos.service.SampleApplicationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.opencv.core.Core;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.*;

@SpringBootApplication
public class SearchForSimilarPhotosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchForSimilarPhotosApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(SampleApplicationService sampleApplicationService) {
		return args -> {
//			String url = ServletUriComponentsBuilder.fromCurrentContextPath().
//					path("/sa/file/FORM33" + DOT + DOCX_EXTENSION).toUriString();
			SampleApplication sa1 = new SampleApplication();
			sa1.setName("FORM33");
			sa1.setText("Выдача и обмен паспорта - истечение срока действия (25, 45 лет)");
			sa1.setUrl("url");
			sampleApplicationService.create(sa1);
//			url = ServletUriComponentsBuilder.fromCurrentContextPath().
//					path("/sa/file/FORM44" + DOT + DOCX_EXTENSION).toUriString();
			SampleApplication sa2 = new SampleApplication();
			sa2.setName("FORM44");
			sa2.setText("Выдача и обмен паспорта - непригодность для использования");
			sa2.setUrl("url2");
			sampleApplicationService.create(sa2);
		};
	}

}
