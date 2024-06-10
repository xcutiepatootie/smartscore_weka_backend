package com.smartscoreml.smartscoreml_algo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Quiz_Result")
public class QuizResultModel {

    @Id
    private String id;
    private double score;
    private double time;
    private double out_of_focus;
    private double answers_clicked;
    private String quizTakenId;
    private String studentId;

    private double retriesLeft;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getOut_of_focus() {
        return out_of_focus;
    }

    public void setOut_of_focus(double out_of_focus) {
        this.out_of_focus = out_of_focus;
    }

    public double getAnswers_clicked() {
        return answers_clicked;
    }

    public void setAnswers_clicked(double answers_clicked) {
        this.answers_clicked = answers_clicked;
    }

    public String getQuizTakenId() {
        return quizTakenId;
    }

    public void setQuizTakenId(String quizTakenId) {
        this.quizTakenId = quizTakenId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getRetriesLeft() {
        return retriesLeft;
    }

    public void setRetriesLeft(double retriesLeft) {
        this.retriesLeft = retriesLeft;
    }


    private double totalScore;
    private double totalTime;
    private double totalOutOfFocus;
    private double totalAnswersClicked;
    private double totalRetriesLeft;
    private int count;

    public void addScore(double score) {
        totalScore += score;
        count++;
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

    @Override
    public String toString() {
        return "QuizResultModel{" +
                "id='" + id + '\'' +
                ", score=" + score +
                ", time=" + time +
                ", out_of_focus=" + out_of_focus +
                ", answers_clicked=" + answers_clicked +
                ", quizTakenId='" + quizTakenId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", retriesLeft=" + retriesLeft +
                '}';
    }
}
