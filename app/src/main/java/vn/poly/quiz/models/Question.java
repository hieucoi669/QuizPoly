package vn.poly.quiz.models;

public class Question {
    private String id;
    private String question;
    private String correctAnswer;
    private String falseAnswerOne;
    private String falseAnswerTwo;
    private String falseAnswerThree;

    public Question() {
    }

    public Question(String question, String correctAnswer, String falseAnswerOne, String falseAnswerTwo, String falseAnswerThree) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.falseAnswerOne = falseAnswerOne;
        this.falseAnswerTwo = falseAnswerTwo;
        this.falseAnswerThree = falseAnswerThree;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

//    public void setQuestion(String question) {
//        this.question = question;
//    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

//    public void setCorrectAnswer(String correctAnswer) {
//        this.correctAnswer = correctAnswer;
//    }

    public String getFalseAnswerOne() {
        return falseAnswerOne;
    }

//    public void setFalseAnswerOne(String falseAnswerOne) {
//        this.falseAnswerOne = falseAnswerOne;
//    }

    public String getFalseAnswerTwo() {
        return falseAnswerTwo;
    }

//    public void setFalseAnswerTwo(String falseAnswerTwo) {
//        this.falseAnswerTwo = falseAnswerTwo;
//    }

    public String getFalseAnswerThree() {
        return falseAnswerThree;
    }

//    public void setFalseAnswerThree(String falseAnswerThree) {
//        this.falseAnswerThree = falseAnswerThree;
//    }
}
