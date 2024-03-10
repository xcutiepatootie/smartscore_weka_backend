package com.smartscoreml.smartscoreml_algo.controller;

import com.smartscoreml.smartscoreml_algo.service.ClusteringService;
import com.smartscoreml.smartscoreml_algo.service.WekaService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/charts")
public class ChartController {

    @Autowired
    WekaService wekaService;

    @Autowired
    ClusteringService clusteringService;

    @GetMapping("/plot")
    public ResponseEntity<byte[]> plotClusters(String quizId) throws Exception {
        Instances data = wekaService.getWekaInstancesFromDB(quizId);
        SimpleKMeans kMeans = clusteringService.loadSimpleKmeans(data);
        int[] assignments = clusteringService.getClusterAssignments(quizId);

        byte[] imageBytes = generateClusterPlot(data, assignments, kMeans);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

    private byte[] generateClusterPlot(Instances data, int[] assignments, SimpleKMeans kMeans) {
        // Create dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] series = new XYSeries[kMeans.getNumClusters()];
        for (int i = 0; i < series.length; i++) {
            series[i] = new XYSeries("Cluster " + (i + 1));
        }
        for (int i = 0; i < data.numInstances(); i++) {

            series[assignments[i]].add(data.instance(i).value(0), data.instance(i).value(1));
        }
        for (int i = 0; i < series.length; i++) {
            dataset.addSeries(series[i]);
        }

        // Add centroids
        Instances centroids = kMeans.getClusterCentroids();
        XYSeries centroidSeries = new XYSeries("Centroids");
        for (int i = 0; i < centroids.numInstances(); i++) {
            centroidSeries.add(centroids.instance(i).value(0), centroids.instance(i).value(1));
        }
        dataset.addSeries(centroidSeries);

        // Create chart
        JFreeChart chart = ChartFactory.createScatterPlot("Cluster Plot", String.valueOf(data.attribute("score")), String.valueOf(data.attribute("time")), dataset);

        // Convert chart to image bytes
        byte[] imageBytes = null;
        try {
            BufferedImage image = chart.createBufferedImage(800, 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(baos, image);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageBytes;
    }
}

