package com.smartscoreml.smartscoreml_algo.repository;

import com.smartscoreml.smartscoreml_algo.model.QuizTakenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizTakenRepo extends MongoRepository<QuizTakenModel, String> {
    List<QuizTakenModel> findByisDone(boolean isDone);

}
