package com.bsuir.neural_network.search_for_similar_photos.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ApplicationService {
    void registration(Long id, String username) throws IOException;
}
