package com.smartscoreml.smartscoreml_algo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClusteringService {

    //Load Dataset

    @Autowired
    WekaService wekaService;

    public Instances loadData() throws Exception {
        Instances data = wekaService.getWekaInstancesFromDB();
        return data;

    }

    public SimpleKMeans loadSimpleKmeans(Instances data) throws Exception {
        SimpleKMeans sKmeans = new SimpleKMeans();
        sKmeans.setNumClusters(2);
        sKmeans.setMaxIterations(500);
        sKmeans.setDontReplaceMissingValues(true);
        sKmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.RANDOM, SimpleKMeans.TAGS_SELECTION));
        //sKmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION));
        sKmeans.setDistanceFunction(new weka.core.EuclideanDistance());
        sKmeans.setSeed(10);
        sKmeans.setPreserveInstancesOrder(true);

        data.deleteAttributeAt(0);

        // Build the clusterer
        sKmeans.buildClusterer(data);


        return sKmeans;
    }


    public int[] getClusterAssignments(Instances data, SimpleKMeans kMeans) throws Exception {
        return kMeans.getAssignments();
    }

    public int[] getClusterAssignments() throws Exception {
        Instances data = loadData();

        SimpleKMeans sKmeans = loadSimpleKmeans(data);
        sKmeans.getAssignments();
        System.out.println("Assignments: " + Arrays.toString(sKmeans.getAssignments()) + "\n" + sKmeans);


        int[] assignments = getClusterAssignments(data, sKmeans);
        printAverageValues(data, assignments, sKmeans);
        return assignments;

    }

    public void printStudentClusterMap(Instances data, int[] assignments) {
        Map<String, Integer> studentClusterMap = createStudentClusterMap(data, assignments);
        for (Map.Entry<String, Integer> entry : studentClusterMap.entrySet()) {
            System.out.println("Student ID: " + entry.getKey() + " - Cluster: " + entry.getValue());
        }
    }

    public Map<String, Integer> createStudentClusterMap(Instances data, int[] assignments) {
        Map<String, Integer> studentClusterMap = new HashMap<>();
        for (int i = 0; i < data.numInstances(); i++) {
            Instance instance = data.instance(i);
            String studentId = instance.stringValue(data.attribute("studentId"));
            int cluster = assignments[i];
            studentClusterMap.put(studentId, cluster);
        }
        return studentClusterMap;
    }

    // Calculate and print average values for each cluster
    public void printAverageValues(Instances data, int[] assignments, SimpleKMeans kMeans) {
        // Group instances by cluster
        Instances[] clusters = new Instances[kMeans.getNumClusters()];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Instances(data, 0);
        }
        for (int i = 0; i < data.numInstances(); i++) {
            clusters[assignments[i]].add(data.instance(i));
        }

        // Calculate and print average values for each cluster
        for (int i = 0; i < clusters.length; i++) {
            System.out.println("Cluster " + (i + 1) + ":");
            Instances cluster = clusters[i];
            for (int j = 0; j < cluster.numAttributes(); j++) {
                if (cluster.attribute(j).isNumeric()) {
                    double mean = cluster.meanOrMode(j);
                    System.out.println("Attribute " + cluster.attribute(j).name() + ": " + mean);
                }
            }
            System.out.println();
        }

    }


    public int runFindElbowPoint() throws Exception {
        // Perform KMeans clustering for various values of k
        int maxK = 10; // Maximum number of clusters to try
        double[] wcss = new double[maxK];

        for (int k = 1; k <= maxK; k++) {
            SimpleKMeans kmeans = new SimpleKMeans();
            Instances data = loadData();
            kmeans.setNumClusters(k);
            data.deleteAttributeAt(0);
            kmeans.buildClusterer(data);

            // Calculate within-cluster sum of squares
            wcss[k - 1] = kmeans.getSquaredError();
        }

        // Find the elbow point
        int elbowPoint = findElbowPoint(wcss);
        System.out.println("Elbow point found at k = " + elbowPoint);
        return elbowPoint;
    }

    private static int findElbowPoint(double[] wcss) {
        // Start from the second element and find the point where the slope changes most
        double maxSlope = Double.MIN_VALUE;
        int elbowPoint = 0;

        for (int i = 1; i < wcss.length; i++) {
            double slope = wcss[i - 1] - wcss[i];
            if (slope > maxSlope) {
                maxSlope = slope;
                elbowPoint = i;
            }
        }

        return elbowPoint + 1;
    }
}

