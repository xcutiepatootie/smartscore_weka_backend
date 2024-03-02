package com.smartscoreml.smartscoreml_algo.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
@Service
public class ClusteringService {

    //Load Dataset

    @Autowired
    WekaService wekaService;

    public Instances loadData() throws Exception{
        Instances data = wekaService.getWekaInstancesFromDB();
        return data;

    }

    public SimpleKMeans loadSimpleKmeans(Instances data)throws Exception{
        SimpleKMeans sKmeans = new SimpleKMeans();
        sKmeans.setNumClusters(2);
        sKmeans.setPreserveInstancesOrder(true);

        data.deleteAttributeAt(0);

        // Build the clusterer
        sKmeans.buildClusterer(data);

        return sKmeans;
    }


    public int[] getClusterAssignments(Instances data, SimpleKMeans kMeans) throws Exception {
        return kMeans.getAssignments();
    }

    public int[] getClusterAssignments() throws Exception{
        Instances data = loadData();

        SimpleKMeans sKmeans = loadSimpleKmeans(data);

        int[] assignments = getClusterAssignments(data, sKmeans);
        printAverageValues(data,assignments,sKmeans);
        return assignments;

    }

    public void printStudentClusterMap(Instances data, int[] assignments){
        Map<String, Integer> studentClusterMap = createStudentClusterMap(data,assignments);
        for (Map.Entry<String, Integer> entry : studentClusterMap.entrySet()) {
            System.out.println("Student ID: " + entry.getKey() + " - Cluster: " + entry.getValue());
        }
    }

    public Map<String, Integer> createStudentClusterMap(Instances data, int[] assignments) {
        Map<String, Integer> studentClusterMap = new HashMap<>();
        for(int i = 0; i < data.numInstances(); i++){
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

}
