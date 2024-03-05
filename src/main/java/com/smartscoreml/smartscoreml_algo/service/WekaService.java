package com.smartscoreml.smartscoreml_algo.service;

import com.smartscoreml.smartscoreml_algo.model.QuizResultModel;
import com.smartscoreml.smartscoreml_algo.model.QuizTakenModel;
import com.smartscoreml.smartscoreml_algo.model.StudentModel;
import com.smartscoreml.smartscoreml_algo.repository.QuizResultRepo;
import com.smartscoreml.smartscoreml_algo.repository.QuizTakenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.*;

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
        //System.out.println(resultData);

        List<QuizTakenModel> notDone_takenData = takenRepo.findByisDone(false);
        // System.out.println(notDone_takenData);

        // Create a map of quizTakenId to QuizTakenModel
        Map<String, QuizTakenModel> takenMap = new HashMap<>();
        for (QuizTakenModel takenModel : takenData) {
            takenMap.put(takenModel.getId(), takenModel);
        }

        Map<String, QuizResultModel> resultMap = new HashMap<>();
        for (QuizResultModel resultModel : resultData) {
            // Create a key using the combination of quizTakenId and studentId
            //String key = resultModel.getQuizTakenId() + "_" + resultModel.getStudentId();
            resultMap.put(resultModel.getStudentId(), resultModel);
        }
        // Print out the map
        for (Map.Entry<String, QuizResultModel> entry : resultMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println(resultMap.size());

        // Create a set of all (quizTakenId, studentId) pairs from notDone_takenData
        Set<String> notDonePairs = new HashSet<>();
        for (QuizTakenModel taken : notDone_takenData) {
            String pair = taken.getId() + "_" + taken.getStudentId();
            notDonePairs.add(pair);
        }

        // System.out.println("Set: " + notDonePairs);


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
        //  System.out.println(resultData);
        // System.out.println("New " + resultMap.size());

        // Iterate through QuizResultModel and compare with QuizTakenModel
        for (Map.Entry<String, QuizTakenModel> entry : takenMap.entrySet()) {
            String key = entry.getKey(); // Get the key
            QuizTakenModel value = entry.getValue(); // Get the value

            // Print key and value
            //     System.out.println("Key: " + key + ", Value: " + value);
        }
        System.out.println("-------");
        for (Map.Entry<String, QuizResultModel> entry : resultMap.entrySet()) {
            String key = entry.getKey(); // Get the key
            QuizResultModel value = entry.getValue(); // Get the value

            // Print key and value
            //    System.out.println("Key: " + key + ", Value: " + value);
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
                //     System.out.println("QuizResultModel: " + resultModel);
            }
        }
        System.out.println(resultMap.size());
        System.out.println(takenMap.size());

        // Create a new map to store student-wise data
        Map<String, StudentModel> studentData = new HashMap<>();

// Iterate through QuizResultModel and populate studentData
        for (QuizResultModel resultModel : resultData) {
            String studentId = resultModel.getStudentId();
            if (studentData.containsKey(studentId)) {
                StudentModel student = studentData.get(studentId);
                student.addScore(resultModel.getScore());
                student.addTime(resultModel.getTime());
                student.addOutOfFocus(resultModel.getOut_of_focus());
                student.addAnswersClicked(resultModel.getAnswers_clicked());
                student.addRetriesLeft(resultModel.getRetriesLeft());
                student.incrementCount();
            } else {
                StudentModel student = new StudentModel();
                student.setStudentId(resultModel.getStudentId());
                student.addScore(resultModel.getScore());
                student.addTime(resultModel.getTime());
                student.addOutOfFocus(resultModel.getOut_of_focus());
                student.addAnswersClicked(resultModel.getAnswers_clicked());
                student.addRetriesLeft(resultModel.getRetriesLeft());
                student.incrementCount();
                studentData.put(studentId, student);
            }
        }

        // Calculate and print averages for each student
        for (Map.Entry<String, StudentModel> entry : studentData.entrySet()) {
            String studentId = entry.getKey();
            StudentModel student = entry.getValue();
            System.out.println("Student ID: " + studentId);
            System.out.println("Average Score: " + String.format("%.2f", student.getAverageScore()));
            System.out.println("Average Time: " + String.format("%.2f", student.getAverageTime()));
            System.out.println("Average Out of Focus: " + String.format("%.2f", student.getAverageOutOfFocus()));
            System.out.println("Average Answers Clicked: " + String.format("%.2f", student.getAverageAnswersClicked()));
            System.out.println("Average Retries Left: " + String.format("%.2f", student.getAverageRetriesLeft()));
            System.out.println("Count in db: "+student.getCount());
            System.out.println();

        }

        for (Map.Entry<String, StudentModel> entry : studentData.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        List<StudentModel> studentList = new ArrayList<>();
        for (Map.Entry<String, StudentModel> entry : studentData.entrySet()) {
            String studentId = entry.getKey();
            studentList.add(entry.getValue());
        }
        System.out.println("=================================================");
        for (StudentModel entry : studentList) {
            System.out.println("Key: " + entry.getStudentId() + ", Value: " + entry.toString());
        }





        System.out.println(resultData.size());
       // System.out.println(resultData);

        Instances wekaInstance = transformToWekaInstances(studentList);
        return wekaInstance;
    }
    // Method to calculate the average results of each student


    private Instances transformToWekaInstances(List<StudentModel> mongoData) {
        // Perform transformation from MongoDB data to Weka Instances
        // Example: Create attribute structure and populate Instances
        ArrayList<Attribute> attributes = new ArrayList<>();

        FastVector studentIdValues = new FastVector();
        for (StudentModel entity : mongoData) {
            if (!studentIdValues.contains(entity.getStudentId())) {
                studentIdValues.addElement(entity.getStudentId());
            }
        }
        attributes.add(new Attribute("studentId", studentIdValues));

        //  attributes.add(new Attribute("studentId", (ArrayList<String>) null)); // Nominal attribute for string values
        attributes.add(new Attribute("score")); // Numerical attribute
        attributes.add(new Attribute("time"));
        attributes.add(new Attribute("out_of_focus"));
        attributes.add(new Attribute("answers_clicked"));
        attributes.add(new Attribute("retriesLeft"));


        Instances wekaInstances = new Instances("Smartscore_Clustering", attributes, mongoData.size());
        // Add attributes to wekaInstances
        for (StudentModel entity : mongoData) {
            double[] values = new double[wekaInstances.numAttributes()];
            // Populate the values array with data from QuizTakenModel
            values[0] = studentIdValues.indexOf(entity.getStudentId());
            // values[0] = wekaInstances.attribute("studentId").addStringValue(entity.getStudentId()); // Adding string value
            values[1] = entity.getAverageScore();
            values[2] = entity.getAverageTime();
            values[3] = entity.getAverageOutOfFocus();
            values[4] = entity.getAverageAnswersClicked();
            values[5] = entity.getAverageRetriesLeft();
            wekaInstances.add(new DenseInstance(1.0, values));
        }
        // Example: wekaInstances.add(new Attribute("attributeName"));
        // Iterate through mongoData and add instances
        // Example: wekaInstances.add(new DenseInstance(new double[]{data1, data2, ...}));
        return wekaInstances;
    }


}
