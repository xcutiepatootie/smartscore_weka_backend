package com.smartscoreml.smartscoreml_algo.model;

public class StudentClusterModel {
private String studentId;
private int cluster;

    public StudentClusterModel(String studentId, int cluster) {
        this.studentId = studentId;
        this.cluster = cluster;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
