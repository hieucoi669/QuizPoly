package vn.poly.quiz.models;

@SuppressWarnings("ALL")
public class PlayerDetail {
    private String username;
    private int totalPlayed;
    private int totalTimePlayed;
    private int totalQuestionAnswered;
    private int totalCorrectAnswer;

    public PlayerDetail() {
    }

    public PlayerDetail(String username, int totalPlayed, int totalTimePlayed,
                        int totalQuestionAnswered, int totalCorrectAnswer) {
        this.username = username;
        this.totalPlayed = totalPlayed;
        this.totalTimePlayed = totalTimePlayed;
        this.totalQuestionAnswered = totalQuestionAnswered;
        this.totalCorrectAnswer = totalCorrectAnswer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalPlayed() {
        return totalPlayed;
    }

    public void setTotalPlayed(int totalPlayed) {
        this.totalPlayed = totalPlayed;
    }

    public int getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public void setTotalTimePlayed(int totalTimePlayed) {
        this.totalTimePlayed = totalTimePlayed;
    }

    public int getTotalQuestionAnswered() {
        return totalQuestionAnswered;
    }

    public void setTotalQuestionAnswered(int totalQuestionAnswered) {
        this.totalQuestionAnswered = totalQuestionAnswered;
    }

    public int getTotalCorrectAnswer() {
        return totalCorrectAnswer;
    }

    public void setTotalCorrectAnswer(int totalCorrectAnswer) {
        this.totalCorrectAnswer = totalCorrectAnswer;
    }
}
