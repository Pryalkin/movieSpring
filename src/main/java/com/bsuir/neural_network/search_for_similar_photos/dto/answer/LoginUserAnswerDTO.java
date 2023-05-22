package com.bsuir.neural_network.search_for_similar_photos.dto.answer;

import lombok.Data;

@Data
public class LoginUserAnswerDTO {

    private String username;
    private String role;
    private String[] authorities;

}
