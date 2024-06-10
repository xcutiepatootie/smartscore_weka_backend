package com.smartscoreml.smartscoreml_algo.model;

import java.util.Map;

public class ClusterAverageValues {
    private int clusterNumber;
    private Map<String, Double> attributeAverages;

    public ClusterAverageValues(int clusterNumber, Map<String, Double> attributeAverages) {
        this.clusterNumber = clusterNumber;
        this.attributeAverages = attributeAverages;
    }

    public int getClusterNumber() {
        return clusterNumber;
    }

    public void setClusterNumber(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    public Map<String, Double> getAttributeAverages() {
        return attributeAverages;
    }

    public void setAttributeAverages(Map<String, Double> attributeAverages) {
        this.attributeAverages = attributeAverages;
    }

}
