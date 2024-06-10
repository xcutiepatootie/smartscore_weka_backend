package com.smartscoreml.smartscoreml_algo;

import com.smartscoreml.smartscoreml_algo.service.ClusteringService;
import com.smartscoreml.smartscoreml_algo.service.WekaService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartscoremlAlgoApplication {

	@Autowired
	WekaService wekaService;

	@Autowired
	ClusteringService clusteringService;

	public static void main(String[] args) {
		SpringApplication.run(SmartscoremlAlgoApplication.class, args);
	}

	@PostConstruct
	public void init() throws Exception {
		// Now wekaService should be initialized by Spring


		System.out.println("==========================================");
		/*int[] assignments = clusteringService.getClusterAssignments();

		clusteringService.printStudentClusterMap(wekaInstances,assignments);

		List<StudentClusterModel> studentClusters = new ArrayList<>();
		Map<String, Integer> studentClusterMap = clusteringService.createStudentClusterMap(wekaInstances, assignments);

		clusteringService.runFindElbowPoint();*/





	}

}
