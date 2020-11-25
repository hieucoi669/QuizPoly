package com.example.quizpoly.Models;

public class User {
    private String id;
    private String username;
    private String password;
    private String stringUri;
    private String displayname;

    public User() {
    }

    public User(String id, String username, String password, String stringUri, String displayname) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.stringUri = stringUri;
        this.displayname = displayname;
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

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }
}
