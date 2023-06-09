package com.bsuir.neural_network.search_for_similar_photos.service.impl;

import com.bsuir.neural_network.search_for_similar_photos.model.SampleApplication;
import com.bsuir.neural_network.search_for_similar_photos.repository.SampleApplicationRepository;
import com.bsuir.neural_network.search_for_similar_photos.service.SampleApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SampleApplicationServiceImpl implements SampleApplicationService {

    private final SampleApplicationRepository sampleApplicationRepository;


    @Override
    public void create(SampleApplication sa) {
        sampleApplicationRepository.save(sa);
    }

    @Override
    public List<SampleApplication> getAll() {
        return sampleApplicationRepository.findAll();
    }
}
