package com.smartscoreml.smartscoreml_algo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Quiz_Result")
public class QuizResultModel {

    @Id
    private String id;
    private int score;
    private int time;
    private int out_of_focus;
    private int answers_clicked;
    private String quizTakenId;
    private String studentId;

    private int retriesLeft;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getOut_of_focus() {
        return out_of_focus;
    }

    public void setOut_of_focus(int out_of_focus) {
        this.out_of_focus = out_of_focus;
    }

    public int getAnswers_clicked() {
        return answers_clicked;
    }

    public void setAnswers_clicked(int answers_clicked) {
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

    public int getRetriesLeft() {
        return retriesLeft;
    }

    public void setRetriesLeft(int retriesLeft) {
        this.retriesLeft = retriesLeft;
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
