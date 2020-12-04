package vn.poly.quiz.models;


public class QuestionRate {
    private String question;
    private int rate;

    public QuestionRate(String question, int rate) {
        this.question = question;
        this.rate = rate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
