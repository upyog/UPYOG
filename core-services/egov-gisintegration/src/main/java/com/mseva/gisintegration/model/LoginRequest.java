package com.mseva.gisintegration.model;

public class LoginRequest {
    private String username;
    private String password; // MD5 encrypted password

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
}
