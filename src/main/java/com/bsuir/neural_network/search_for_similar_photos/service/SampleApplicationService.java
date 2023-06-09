package com.bsuir.neural_network.search_for_similar_photos.service;

import com.bsuir.neural_network.search_for_similar_photos.model.SampleApplication;

import java.util.List;

public interface SampleApplicationService {
    void create(SampleApplication sa);

    List<SampleApplication> getAll();
}
