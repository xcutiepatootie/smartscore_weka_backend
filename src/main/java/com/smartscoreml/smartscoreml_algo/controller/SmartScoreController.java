package com.smartscoreml.smartscoreml_algo.controller;

import com.smartscoreml.smartscoreml_algo.service.ClusteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SmartScoreController {
    @Autowired
    private ClusteringService clusteringService;

  /*  @GetMapping("/assignments")
    public List<StudentClusterModel> getClusterAssignments() throws Exception{

    }*/
}
