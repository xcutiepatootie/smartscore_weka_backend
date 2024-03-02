package com.smartscoreml.smartscoreml_algo.service;

import com.smartscoreml.smartscoreml_algo.model.QuizResultModel;
import com.smartscoreml.smartscoreml_algo.model.QuizTakenModel;
import com.smartscoreml.smartscoreml_algo.repository.QuizResultRepo;
import com.smartscoreml.smartscoreml_algo.repository.QuizTakenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.*;

@Service
public class WekaService {

    @Autowired
    QuizTakenRepo takenRepo;

    @Autowired
    QuizResultRepo resultRepo;

    public Instances getWekaInstancesFromDB() {
        List<QuizTakenModel> takenData = takenRepo.findByisDone(true);
        List<QuizResultModel> resultData = resultRepo.findAll();

        List<QuizTakenModel> notDone_takenData = takenRepo.findByisDone(false);
        System.out.println(notDone_takenData);

        // Create a map of quizTakenId to QuizTakenModel
        Map<String, QuizTakenModel> takenMap = new HashMap<>();
        for (QuizTakenModel takenModel : takenData) {
            takenMap.put(takenModel.getId(), takenModel);
        }

        Map<String, QuizResultModel> resultMap = new HashMap<>();
        for (QuizResultModel resultModel : resultData) {
            // Create a key using the combination of quizTakenId and studentId
            //String key = resultModel.getQuizTakenId() + "_" + resultModel.getStudentId();
            resultMap.put(resultModel.getId(), resultModel);
        }
        System.out.println(resultMap.size());

        // Create a set of all (quizTakenId, studentId) pairs from notDone_takenData
        Set<String> notDonePairs = new HashSet<>();
        for (QuizTakenModel taken : notDone_takenData) {
            String pair = taken.getId() + "_" + taken.getStudentId();
            notDonePairs.add(pair);
        }

        System.out.println("Set: " + notDonePairs);


        // Remove QuizResultModel objects from resultData where quizTakenId and studentId match any entry in notDone_takenData
        Iterator<QuizResultModel> iterator = resultData.iterator();
        while (iterator.hasNext()) {
            QuizResultModel result = iterator.next();
            String pair = result.getQuizTakenId() + "_" + result.getStudentId();
            if (notDonePairs.contains(pair)) {
                iterator.remove();
            }
        }

        // Now resultData contains only the QuizResultModel objects where quizTakenId and studentId do not match any entry in notDone_takenData
        System.out.println(resultData);
        System.out.println("New " + resultMap.size());

        // Iterate through QuizResultModel and compare with QuizTakenModel
        for (Map.Entry<String, QuizTakenModel> entry : takenMap.entrySet()) {
            String key = entry.getKey(); // Get the key
            QuizTakenModel value = entry.getValue(); // Get the value

            // Print key and value
            System.out.println("Key: " + key + ", Value: " + value);
        }
        System.out.println("-------");
        for (Map.Entry<String, QuizResultModel> entry : resultMap.entrySet()) {
            String key = entry.getKey(); // Get the key
            QuizResultModel value = entry.getValue(); // Get the value

            // Print key and value
            System.out.println("Key: " + key + ", Value: " + value);
        }
        System.out.println("-------");

        // Create a new map to store retriesLeft for each quizTakenId
        Map<String, Integer> retriesLeftMap = new HashMap<>();

        // Iterate through QuizTakenModel and populate retriesLeftMap
        for (QuizTakenModel takenModel : takenMap.values()) {
            retriesLeftMap.put(takenModel.getId(), takenModel.getRetriesLeft());
        }

        // Iterate through QuizResultModel and add retriesLeft from retriesLeftMap
        for (QuizResultModel resultModel : resultMap.values()) {
            String quizTakenId = resultModel.getQuizTakenId();
            Integer retriesLeft = retriesLeftMap.get(quizTakenId);
            if (retriesLeft != null) {
                // Add retriesLeft to QuizResultModel (or perform any action needed)
                // For example:
                resultModel.setRetriesLeft(retriesLeft);
                System.out.println("QuizResultModel: " + resultModel);
            }
        }
        System.out.println(resultMap.size());
        System.out.println(takenMap.size());


        Instances wekaInstance = transformToWekaInstances(resultData);
        return wekaInstance;
    }

    private Instances transformToWekaInstances(List<QuizResultModel> mongoData) {
        // Perform transformation from MongoDB data to Weka Instances
        // Example: Create attribute structure and populate Instances
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("studentId", (ArrayList<String>) null)); // Nominal attribute for string values
        attributes.add(new Attribute("score")); // Numerical attribute
        attributes.add(new Attribute("time"));
        attributes.add(new Attribute("out_of_focus"));
        attributes.add(new Attribute("answers_clicked"));
        attributes.add(new Attribute("retriesLeft"));


        Instances wekaInstances = new Instances("Smartscore_Clustering", attributes, mongoData.size());
        // Add attributes to wekaInstances
        for (QuizResultModel entity : mongoData) {
            double[] values = new double[wekaInstances.numAttributes()];
            // Populate the values array with data from QuizTakenModel
            values[0] = wekaInstances.attribute("studentId").addStringValue(entity.getStudentId()); // Adding string value
            values[1] = entity.getScore();
            values[2] = entity.getTime();
            values[3] = entity.getOut_of_focus();
            values[4] = entity.getAnswers_clicked();
            values[5] = entity.getRetriesLeft();
            wekaInstances.add(new DenseInstance(1.0, values));
        }
        // Example: wekaInstances.add(new Attribute("attributeName"));
        // Iterate through mongoData and add instances
        // Example: wekaInstances.add(new DenseInstance(new double[]{data1, data2, ...}));
        return wekaInstances;
    }


}
