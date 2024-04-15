package com.smartscoreml.smartscoreml_algo.controller;

import com.smartscoreml.smartscoreml_algo.model.ClusterAverageValues;
import com.smartscoreml.smartscoreml_algo.model.StudentClusterModel;
import com.smartscoreml.smartscoreml_algo.model.StudentModel;
import com.smartscoreml.smartscoreml_algo.service.ClusteringService;
import com.smartscoreml.smartscoreml_algo.service.WekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000","https://prod-stage--smartscore.netlify.app","https://smartscore.netlify.app","https://main--smartscore.netlify.app"})

public class SmartScoreController {
    @Autowired
    private ClusteringService clusteringService;

    @Autowired
    private WekaService wekaService;


    @GetMapping("/assignments")
    public List<StudentClusterModel> getClusterAssignments(@RequestParam("quizId") String quizId) throws Exception {
        Instances data = clusteringService.loadData(quizId); // Implement loadData method as needed
        System.out.println(data);
        int[] assignments = clusteringService.getClusterAssignments(quizId); // Implement buildClustererAndGetAssignments method as needed

        List<StudentClusterModel> studentClusters = new ArrayList<>();
        Map<String, Integer> studentClusterMap = clusteringService.createStudentClusterMap(data, assignments);

        for (Map.Entry<String, Integer> entry : studentClusterMap.entrySet()) {
            studentClusters.add(new StudentClusterModel(entry.getKey(), entry.getValue()));
        }

        return studentClusters;
    }

    @GetMapping("/student_records")
    public List<StudentModel> getStudentRecords(@RequestParam("quizId") String quizId) throws Exception {
        Instances data = clusteringService.loadData(quizId); // Implement loadData method as needed
        System.out.println(data);

        Map<String, StudentModel> studentDataMap = wekaService.processQuizResults(quizId);

        List<StudentModel> studentRecords = new ArrayList<>(studentDataMap.values());


        return studentRecords;
    }

    @GetMapping("/student_records_charts")
    public Map<String, List<StudentModel>> getStudentRecords_charts(@RequestParam("quizId") String quizId, @RequestParam("studentId") String studentId) throws Exception {
        Instances data = clusteringService.loadData(quizId); // Implement loadData method as needed
        System.out.println(data);

        Map<String, StudentModel> studentDataMap = wekaService.processQuizResults(quizId);

        List<StudentModel> studentRecords = new ArrayList<>(studentDataMap.values());

        List<StudentModel> userValue = new ArrayList<>();
        userValue.add(studentDataMap.get(studentId));

        // Sort the student records by average score in descending order
        List<StudentModel> sortedRecords_score = studentRecords.stream()
                .sorted(Comparator.comparingDouble(StudentModel::getAverageScore).reversed())
                .toList();

        List<StudentModel> distinctHighestScores = new ArrayList<>();

        // Iterate through sorted records and add only distinct records to the result list
        for (StudentModel record : sortedRecords_score) {
            if (distinctHighestScores.stream().noneMatch(student -> Math.abs(student.getAverageScore() - record.getAverageScore()) < 0.0001)) {
                distinctHighestScores.add(record);
            }
            if (distinctHighestScores.size() == 5) {
                break;
            }
        }

        // Sort the student records by average time in descending order
        List<StudentModel> sortedRecords_time = studentRecords.stream()
                .sorted(Comparator.comparingDouble(StudentModel::getAverageTime))
                .toList();

        List<StudentModel> distinctLowestTime = new ArrayList<>();

        // Iterate through sorted records and add only distinct records to the result list
        for (StudentModel record : sortedRecords_time) {
            if (distinctLowestTime.stream().noneMatch(student -> Math.abs(record.getAverageTime() - student.getAverageTime()) < 0.0001)) {
                distinctLowestTime.add(record);
            }
            if (distinctLowestTime.size() == 5) {
                break;
            }
        }

        // Sort the student records by average answers clicked in descending order
        List<StudentModel> sortedRecords_answersClicked = studentRecords.stream()
                .sorted(Comparator.comparingDouble(StudentModel::getAverageAnswersClicked))
                .toList();

        List<StudentModel> distinctAnswersClicked = new ArrayList<>();

        // Iterate through sorted records and add only distinct records to the result list
        for (StudentModel record : sortedRecords_answersClicked) {
            if (distinctAnswersClicked.stream().noneMatch(student -> Math.abs(record.getAverageAnswersClicked() - student.getAverageAnswersClicked()) < 0.0001)) {
                distinctAnswersClicked.add(record);
            }
            if (distinctAnswersClicked.size() == 5) {
                break;
            }
        }

        // Sort the student records by average out of focus in descending order
        List<StudentModel> sortedRecords_outOfFocus = studentRecords.stream()
                .sorted(Comparator.comparingDouble(StudentModel::getAverageOutOfFocus))
                .toList();

        List<StudentModel> distinctOutOfFocus = new ArrayList<>();

        // Iterate through sorted records and add only distinct records to the result list
        for (StudentModel record : sortedRecords_outOfFocus) {
            if (distinctOutOfFocus.stream().noneMatch(student -> Math.abs(record.getAverageOutOfFocus() - student.getAverageOutOfFocus()) < 0.0001)) {
                distinctOutOfFocus.add(record);
            }
            if (distinctOutOfFocus.size() == 5) {
                break;
            }
        }

        // Sort the student records by average answers clicked in descending order
        List<StudentModel> sortedRecords_retriesLeft = studentRecords.stream()
                .sorted(Comparator.comparingDouble(StudentModel::getAverageRetriesLeft).reversed())
                .toList();

        List<StudentModel> distinctRetriesLeft = new ArrayList<>();

        // Iterate through sorted records and add only distinct records to the result list
        for (StudentModel record : sortedRecords_retriesLeft) {
            if (distinctRetriesLeft.stream().noneMatch(student -> Math.abs(student.getAverageRetriesLeft() - record.getAverageRetriesLeft()) < 0.0001)) {
                distinctRetriesLeft.add(record);
            }
            if (distinctRetriesLeft.size() == 5) {
                break;
            }
        }

        Map<String, List<StudentModel>> result = new HashMap<>();
        result.put("userValue", userValue);
        result.put("scores", distinctHighestScores);
        result.put("time", distinctLowestTime);
        result.put("answersclicked", distinctAnswersClicked);
        result.put("outoffocus", distinctOutOfFocus);
        result.put("retriesleft", distinctRetriesLeft);
        return result;
    }


    @GetMapping("/cluster/average-values")
    public ClusterAverageValues[] getClusterAverageValues(@RequestParam("quizId") String quizId) throws Exception {
        // Fetch instances and assignments from somewhere (e.g., a database or generated data)
        Instances data = clusteringService.loadData(quizId);
        int[] assignments = clusteringService.getClusterAssignments(quizId); // Fetch assignments here
        SimpleKMeans kMeans = clusteringService.loadSimpleKmeans(data); // Initialize your KMeans model here

        // Calculate and return average values for each cluster
        return clusteringService.calculateAverageValues(data, assignments, kMeans);
    }
}
