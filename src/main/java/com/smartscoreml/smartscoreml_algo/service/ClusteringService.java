package com.smartscoreml.smartscoreml_algo.service;

import com.smartscoreml.smartscoreml_algo.model.ClusterAverageValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.clusterers.DBSCAN;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClusteringService {

    //Load Dataset

    @Autowired
    WekaService wekaService;


    public Instances loadData(String quizId) throws Exception {
        Instances data = wekaService.getWekaInstancesFromDB(quizId);
        return data;

    }


    public SimpleKMeans loadSimpleKmeans(Instances data) throws Exception {
        SimpleKMeans sKmeans = new SimpleKMeans();
        int no_Clusters = runFindElbowPoint(data);
        System.out.println("Number of cluster: " + no_Clusters);

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        euclideanDistance.setDontNormalize(true);
        ManhattanDistance manhattanDistance = new ManhattanDistance();
        manhattanDistance.setDontNormalize(true);

        sKmeans.setNumClusters(no_Clusters);
        sKmeans.setMaxIterations(500);
        sKmeans.setDontReplaceMissingValues(true);
        sKmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.RANDOM, SimpleKMeans.TAGS_SELECTION));
        //sKmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION));
        sKmeans.setDistanceFunction(manhattanDistance);
        sKmeans.setDontReplaceMissingValues(true);
        sKmeans.setSeed(10);
        sKmeans.setPreserveInstancesOrder(true);

        data.deleteAttributeAt(0);

        // Build the clusterer
        sKmeans.buildClusterer(data);


        return sKmeans;
    }

    public HierarchicalClusterer loadHierarchical(Instances data) throws Exception {
        HierarchicalClusterer hierarchicalClusterer = new HierarchicalClusterer();
        int no_Clusters = runFindElbowPoint(data);
        String[] options = {"-L", "AVERAGE"};

        ManhattanDistance manhattanDistance = new ManhattanDistance();
        manhattanDistance.setDontNormalize(true);


        hierarchicalClusterer.setOptions(options);
        hierarchicalClusterer.setDistanceFunction(manhattanDistance);
        hierarchicalClusterer.setNumClusters(no_Clusters);

        hierarchicalClusterer.buildClusterer(data);

        return hierarchicalClusterer;
    }

    public DBSCAN loadDBSCAN(Instances data) throws Exception {
        DBSCAN dbscan = new DBSCAN();
        ManhattanDistance manhattanDistance = new ManhattanDistance();

        data.deleteAttributeAt(0);

        // Set parameters
        dbscan.setDistanceFunction(manhattanDistance);
        dbscan.setEpsilon(0.9);
        dbscan.setMinPoints(6);

        dbscan.buildClusterer(data);
        System.out.println("Successfully Loaded DBSCAN");

        System.out.println(dbscan);

        return dbscan;
    }


    public static int[] getClusterAssignments_DBSCAN(Instances data, DBSCAN dbscan) {
        int[] clusterAssignments = new int[data.numInstances()];

        // Get the cluster assignments for each instance and store in the array
        for (int i = 0; i < data.numInstances(); i++) {
            Instance instance = data.instance(i);
            try {
                clusterAssignments[i] = dbscan.clusterInstance(instance);
            } catch (Exception e) {
                clusterAssignments[i] = -1;
            }
        }
        return clusterAssignments;
    }


    public int[] getClusterAssignments_DBSCAN(String quizId) throws Exception {
        Instances data = loadData(quizId);

        DBSCAN dbscan = loadDBSCAN(data);
        int[] assignments = getClusterAssignments_DBSCAN(data, dbscan);
        System.out.println("Assignments: " + Arrays.toString(assignments) + "\n" + dbscan);

        printAverageValues_DBSCAN(data, assignments, dbscan);
        return assignments;
    }


    public int[] getClusterAssignments_hier(Instances data, HierarchicalClusterer hierarchicalClusterer) throws Exception {

        int[] clusterAssignments = new int[data.numInstances()];

        // Get the cluster assignments for each instance and store in the array
        for (int i = 0; i < data.numInstances(); i++) {
            Instance instance = data.instance(i);
            clusterAssignments[i] = hierarchicalClusterer.clusterInstance(instance);
        }

        // Print or process the cluster assignments as needed
        for (int i = 0; i < data.numInstances(); i++) {
            System.out.println("Instance " + i + " is assigned to cluster " + clusterAssignments[i]);
        }
        return clusterAssignments;
    }

    public int[] getClusterAssignments_hier(String quizId) throws Exception {
        Instances data = loadData(quizId);

        HierarchicalClusterer hierarchicalClusterer = loadHierarchical(data);
        System.out.println("Assignments: " + Arrays.toString(getClusterAssignments_hier(data, hierarchicalClusterer)) + "\n" + hierarchicalClusterer);


        int[] assignments = getClusterAssignments_hier(data, hierarchicalClusterer);
        printAverageValues_hier(data, assignments, hierarchicalClusterer);
        return assignments;

    }

    public int[] getClusterAssignments(Instances data, SimpleKMeans kMeans) throws Exception {
        return kMeans.getAssignments();
    }

    public int[] getClusterAssignments(String quizId) throws Exception {
        Instances data = loadData(quizId);

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

    public ClusterAverageValues[] calculateAverageValues(Instances data, int[] assignments, SimpleKMeans kMeans) {
        ClusterAverageValues[] clusterAverages = new ClusterAverageValues[kMeans.getNumClusters()];

        // Initialize cluster averages
        for (int i = 0; i < clusterAverages.length; i++) {
            clusterAverages[i] = new ClusterAverageValues(i + 1, new HashMap<>());
        }

        // Calculate average values for each cluster
        for (int i = 0; i < data.numAttributes(); i++) {
            if (data.attribute(i).isNumeric()) {
                for (int j = 0; j < data.numInstances(); j++) {
                    int clusterIndex = assignments[j];
                    double value = data.instance(j).value(i);
                    clusterAverages[clusterIndex].getAttributeAverages().merge(data.attribute(i).name(),
                            value, (oldValue, newValue) -> (oldValue + newValue) / 2);
                }
            }
        }

        return clusterAverages;
    }


    public void printAverageValues_hier(Instances data, int[] assignments, HierarchicalClusterer hierarchicalClusterer) {
        // Group instances by cluster
        Instances[] clusters = new Instances[hierarchicalClusterer.getNumClusters()];
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

    public void printAverageValues_DBSCAN(Instances data, int[] assignments, DBSCAN dbscan) throws Exception {
        // Group instances by cluster
        Instances[] clusters = new Instances[dbscan.numberOfClusters()];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Instances(data, 0);
        }
        for (int i = 0; i < data.numInstances(); i++) {
            int clusterIndex = assignments[i];
            if (clusterIndex != -1) {
                clusters[clusterIndex].add(data.instance(i));
            }
        }

        // Calculate and print average values for each cluster
        for (int i = 0; i < clusters.length; i++) {
            System.out.println("Cluster " + (i) + ":");
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


    public int runFindElbowPoint(Instances dataParams) throws Exception {
        System.out.println("Data Params: " + dataParams);
        // Perform KMeans clustering for various values of k
        int maxK = 10; // Maximum number of clusters to try
        double[] wcss = new double[maxK];

        for (int k = 1; k <= maxK; k++) {
            SimpleKMeans kmeans = new SimpleKMeans();
            kmeans.setNumClusters(k);

            kmeans.buildClusterer(dataParams);

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

