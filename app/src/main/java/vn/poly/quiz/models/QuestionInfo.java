package vn.poly.quiz.models;

public class QuestionInfo {
    private String id;
    private String question;
    private int numberAnswered;
    private int numberCorrectAnswer;

    public QuestionInfo(String id, String question, int numberAnswered, int numberCorrectAnswer) {
        this.id = id;
        this.question = question;
        this.numberAnswered = numberAnswered;
        this.numberCorrectAnswer = numberCorrectAnswer;
    }

    public QuestionInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberAnswered() {
        return numberAnswered;
    }

    public void setNumberAnswered(int numberAnswered) {
        this.numberAnswered = numberAnswered;
    }

    public int getNumberCorrectAnswer() {
        return numberCorrectAnswer;
    }

    public void setNumberCorrectAnswer(int numberCorrectAnswer) {
        this.numberCorrectAnswer = numberCorrectAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Double getRate(){
        return numberCorrectAnswer*100.0/numberAnswered;
    }
}
