package vn.poly.quiz.models;

public class User {
    private String id;
    private String username;
    private String password;
    private String stringUri;
    private String displayName;

    public User() {
    }

    public User(String id, String username, String password, String stringUri, String displayName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.stringUri = stringUri;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStringUri() {
        return stringUri;
    }

    public void setStringUri(String stringUri) {
        this.stringUri = stringUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
