package vn.poly.quiz.models;

public class QuizResult {
    private int id;
    private String quizID;
    private int questionID;
    private int result;

    public QuizResult(String quizID, int questionID, int result) {
        this.quizID = quizID;
        this.questionID = questionID;
        this.result = result;
    }

    public QuizResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
