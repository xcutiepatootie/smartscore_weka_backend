package com.smartscoreml.smartscoreml_algo.controller;

import com.smartscoreml.smartscoreml_algo.model.StudentClusterModel;
import com.smartscoreml.smartscoreml_algo.service.ClusteringService;
import com.smartscoreml.smartscoreml_algo.service.WekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
