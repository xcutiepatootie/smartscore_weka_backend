package com.smartscoreml.smartscoreml_algo.controller;

import com.smartscoreml.smartscoreml_algo.model.ClusterAverageValues;
import com.smartscoreml.smartscoreml_algo.model.StudentClusterModel;
import com.smartscoreml.smartscoreml_algo.model.StudentModel;
import com.smartscoreml.smartscoreml_algo.service.ClusteringService;
import com.smartscoreml.smartscoreml_algo.service.WekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SmartScoreController {
    @Autowired
    private ClusteringService clusteringService;

    @Autowired
    private WekaService wekaService;


    @GetMapping("/assignments")
    public List<StudentClusterModel> getClusterAssignments(@RequestParam("quizId") String quizId) throws Exception{
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
    public List<StudentModel> getStudentRecords(@RequestParam("quizId") String quizId) throws Exception{
        Instances data = clusteringService.loadData(quizId); // Implement loadData method as needed
        System.out.println(data);

        Map<String,StudentModel> studentDataMap= wekaService.processQuizResults(quizId);

        List<StudentModel> studentRecords = new ArrayList<>(studentDataMap.values());



        return studentRecords;
    }

    @GetMapping("/cluster/average-values")
    public ClusterAverageValues[] getClusterAverageValues(@RequestParam("quizId") String quizId)throws Exception {
        // Fetch instances and assignments from somewhere (e.g., a database or generated data)
        Instances data = clusteringService.loadData(quizId);
        int[] assignments = clusteringService.getClusterAssignments(quizId); // Fetch assignments here
        SimpleKMeans kMeans = clusteringService.loadSimpleKmeans(data); // Initialize your KMeans model here

        // Calculate and return average values for each cluster
        return clusteringService.calculateAverageValues(data, assignments, kMeans);
    }
}
