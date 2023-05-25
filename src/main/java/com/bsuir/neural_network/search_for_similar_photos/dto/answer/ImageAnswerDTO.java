package com.bsuir.neural_network.search_for_similar_photos.dto.answer;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ImageAnswerDTO {

    private Long id;
    private String url;
    private Set<String> keywords = new HashSet<>();

}
