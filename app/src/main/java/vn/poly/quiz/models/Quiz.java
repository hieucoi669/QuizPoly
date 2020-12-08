package vn.poly.quiz.models;

public class Quiz {
    private String key;
    private String imageURL;
    private String username;
    private String displayName;
    private int numCorrectAnswer;
    private int time;

    public Quiz(String key, String imageURL, String username,
                String displayName, int numCorrectAnswer, int time) {
        this.key = key;
        this.imageURL = imageURL;
        this.username = username;
        this.displayName = displayName;
        this.numCorrectAnswer = numCorrectAnswer;
        this.time = time;
    }

    public Quiz() {
    }

    public String getKey() {
        return key;
    }

//    public void setKey(String key) {
//        this.key = key;
//    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
