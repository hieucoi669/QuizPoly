package com.example.quizpoly.Models;

public class Quiz {
    private String quizID;
    private String username;
    private int numCorrectAnswer;
    private int time;

    public Quiz(String quizID, String username, int numCorrectAnswer, int time) {
        this.quizID = quizID;
        this.username = username;
        this.numCorrectAnswer = numCorrectAnswer;
        this.time = time;
    }

    public Quiz(String username, int numCorrectAnswer, int time) {
        this.username = username;
        this.numCorrectAnswer = numCorrectAnswer;
        this.time = time;
    }

    public Quiz() {
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumCorrectAnswer() {
        return numCorrectAnswer;
    }

    public void setNumCorrectAnswer(int numCorrectAnswer) {
        this.numCorrectAnswer = numCorrectAnswer;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
