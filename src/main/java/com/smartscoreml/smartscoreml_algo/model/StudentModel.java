package com.smartscoreml.smartscoreml_algo.model;

public class StudentModel {

    private String studentId;


    private double totalScore;
    private double totalTime;
    private double totalOutOfFocus;
    private double totalAnswersClicked;
    private double totalRetriesLeft;


    private int count;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "StudentModel{" +
                "studentId='" + studentId + '\'' +
                ", totalScore=" + totalScore +
                ", totalTime=" + totalTime +
                ", totalOutOfFocus=" + totalOutOfFocus +
                ", totalAnswersClicked=" + totalAnswersClicked +
                ", totalRetriesLeft=" + totalRetriesLeft +
                ", count=" + count +
                '}';
    }

    public double getTotalOutOfFocus() {
        return totalOutOfFocus;
    }

    public void setTotalOutOfFocus(double totalOutOfFocus) {
        this.totalOutOfFocus = totalOutOfFocus;
    }

    public double getTotalAnswersClicked() {
        return totalAnswersClicked;
    }

    public void setTotalAnswersClicked(double totalAnswersClicked) {
        this.totalAnswersClicked = totalAnswersClicked;
    }

    public double getTotalRetriesLeft() {
        return totalRetriesLeft;
    }

    public void setTotalRetriesLeft(double totalRetriesLeft) {
        this.totalRetriesLeft = totalRetriesLeft;
    }





    public void addScore(double score) {
        totalScore += score;

    }

    public void addTime(double time) {
        totalTime += time;
    }

    public void addOutOfFocus(double outOfFocus) {
        totalOutOfFocus += outOfFocus;
    }

    public void addAnswersClicked(double answersClicked) {
        totalAnswersClicked += answersClicked;
    }

    public void addRetriesLeft(double retriesLeft) {
        totalRetriesLeft += retriesLeft;
    }

    public double getAverageScore() {
        return totalScore / count;
    }

    public double getAverageTime() {
        return totalTime / count;
    }

    public double getAverageOutOfFocus() {
        return totalOutOfFocus / count;
    }

    public double getAverageAnswersClicked() {
        return totalAnswersClicked / count;
    }

    public double getAverageRetriesLeft() {
        return totalRetriesLeft / count;
    }

    public int getCount() {
        return count;
    }



    public void incrementCount() {
         count++;
    }

}
