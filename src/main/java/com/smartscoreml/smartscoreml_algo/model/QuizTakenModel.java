package com.smartscoreml.smartscoreml_algo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "QuizTaken")
public class QuizTakenModel {
    @Id
    private String id;
    private String quizId;
    private String studentId;
    private int retriesLeft;
    private boolean isPerfect;
    private boolean isDone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
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

    public boolean isPerfect() {
        return isPerfect;
    }

    public void setPerfect(boolean perfect) {
        isPerfect = perfect;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return "QuizResultModel{" +
                "id='" + id + '\'' +
                ", quizId='" + quizId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", retriesLeft=" + retriesLeft +
                ", isPerfect=" + isPerfect +
                ", isDone=" + isDone +
                '}';
    }
}
