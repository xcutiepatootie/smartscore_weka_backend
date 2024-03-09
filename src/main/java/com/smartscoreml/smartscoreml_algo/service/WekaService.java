package com.smartscoreml.smartscoreml_algo.service;

import com.smartscoreml.smartscoreml_algo.model.QuizResultModel;
import com.smartscoreml.smartscoreml_algo.model.QuizTakenModel;
import com.smartscoreml.smartscoreml_algo.model.StudentModel;
import com.smartscoreml.smartscoreml_algo.repository.QuizResultRepo;
import com.smartscoreml.smartscoreml_algo.repository.QuizTakenRepo;
import org.bson.types.ObjectId;
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

        //System.out.println(takenData);

        // 65d75dcf67e94549c1fad778

        ObjectId quizId = new ObjectId("65e5d5b825d258c080b78f63");

        // Get QuizTaken using QuizId
        List<QuizTakenModel> byQuiz = takenRepo.findByQuizId(quizId);
        Map<String, QuizTakenModel> byquizMap = new HashMap<>();
        for (QuizTakenModel byquizMappedList : byQuiz) {
            byquizMap.put(byquizMappedList.getId(), byquizMappedList);
        }
        System.out.println("============================");
        System.out.println("Get quizTaken where quizId is : " + quizId);
        /*for (Map.Entry<String, QuizTakenModel> entry : byquizMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println("============================");*/


        //Get All Results from QuizResults
        Map<String, QuizResultModel> allResultMap = new HashMap<>();
        for (QuizResultModel allResultList : resultData) {
            allResultMap.put(allResultList.getId(), allResultList);
        }
        System.out.println();
      /*  System.out.println("============================");
        System.out.println("Get All Results ");
        for (Map.Entry<String, QuizResultModel> entry : allResultMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println("============================");*/

        // Create a new map to store QuizResultModel objects associated with QuizTakenModel
        Map<String, QuizResultModel> filteredResultsMap = new HashMap<>();

        // Iterate over each entry in the second map
        for (Map.Entry<String, QuizResultModel> entry : allResultMap.entrySet()) {
            String quizTakenId = entry.getValue().getQuizTakenId(); // Get the quizTakenId from the QuizResultModel

            // Check if the quizTakenId exists in the first map
            if (byquizMap.containsKey(quizTakenId)) {
                // If the quizTakenId exists in the first map, add the QuizResultModel to the filteredResultsMap
                filteredResultsMap.put(entry.getKey(), entry.getValue());
            }
        }

        // Create a new map to store retriesLeft for each quizTakenId
        Map<String, Integer> retriesLeftMap = new HashMap<>();

        // Iterate through QuizTakenModel and populate retriesLeftMap
        for (QuizTakenModel takenModel : byquizMap.values()) {
            retriesLeftMap.put(takenModel.getId(), takenModel.getRetriesLeft());
        }

        // Iterate through QuizResultModel and add retriesLeft from retriesLeftMap
        for (QuizResultModel resultModel : filteredResultsMap.values()) {
            String quizTakenId = resultModel.getQuizTakenId();
            Integer retriesLeft = retriesLeftMap.get(quizTakenId);
            if (retriesLeft != null) {
                // Add retriesLeft to QuizResultModel (or perform any action needed)
                // For example:
                resultModel.setRetriesLeft(retriesLeft);
                //     System.out.println("QuizResultModel: " + resultModel);
            }
        }

// Print or use the filteredResultsMap as needed
        System.out.println("============================");
        System.out.println("Filtered Results Based on QuizTaken");
        for (Map.Entry<String, QuizResultModel> entry : filteredResultsMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println("============================");

        // Create a new list to store filtered QuizResultModel objects
        List<QuizResultModel> filteredResultsList = new ArrayList<>();

// Iterate over each entry in the filteredResultsMap and add the values to the list
        for (Map.Entry<String, QuizResultModel> entry : filteredResultsMap.entrySet()) {
            filteredResultsList.add(entry.getValue());
        }






        // Create a new map to store student-wise data
        Map<String, StudentModel> studentData = new HashMap<>();

        // Iterate through QuizResultModel and populate studentData
        for (QuizResultModel resultModel : filteredResultsList) {
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
            System.out.println("Count in db: " + student.getCount());
            System.out.println();

        }

        for (Map.Entry<String, StudentModel> entry : studentData.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // Create a new list to store StudentModel objects
        List<StudentModel> studentList = new ArrayList<>();

// Iterate over each entry in the studentData map and add the values to the list
        for (StudentModel student : studentData.values()) {
            studentList.add(student);
        }

// Print or use the studentList as needed
        System.out.println("============================");
        System.out.println("Student List:");
        for (StudentModel student : studentList) {
            System.out.println("Student: " + student);
        }
        System.out.println("============================");


        System.out.println("Byquiz size: " + byquizMap.size());
        System.out.println("ALl size: " + allResultMap.size());
        System.out.println("filtered size: "+ filteredResultsMap.size());
        System.out.println("final size: "+studentData.size());
        System.out.println("Students List size: "+studentList.size());

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
