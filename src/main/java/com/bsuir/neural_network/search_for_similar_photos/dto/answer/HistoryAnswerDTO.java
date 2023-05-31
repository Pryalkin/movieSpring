package com.bsuir.neural_network.search_for_similar_photos.dto.answer;

import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import lombok.Data;

@Data
public class HistoryAnswerDTO {

    private Long id;
    private String url;
    private String[] words;

}
