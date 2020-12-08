package vn.poly.quiz.models;

import java.io.Serializable;

public class User implements Serializable {
    private String uniqueLogin;
    private String username;
    private String password;
    private String displayName;
    private String imageURL;
    private String auth;

    public User() {
    }

    public User(String username, String password, String auth) {
        this.username = username;
        this.password = password;
        this.auth = auth;
    }

    public User(String username, String password, String displayName, String imageURL, String auth) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.imageURL = imageURL;
        this.auth = auth;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getUniqueLogin() {
        return uniqueLogin;
    }

    public void setUniqueLogin(String uniqueLogin) {
        this.uniqueLogin = uniqueLogin;
    }
}
