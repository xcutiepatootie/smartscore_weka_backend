package com.smartscoreml.smartscoreml_algo.repository;

import com.smartscoreml.smartscoreml_algo.model.QuizResultModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizResultRepo extends MongoRepository<QuizResultModel, String> {

}
