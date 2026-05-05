package ru.netology.FinalCloud.Users.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponse {

    @JsonProperty("auth-token")
    private String authToken;

    public JwtResponse(@JsonProperty("auth-token") String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken(String authToken) {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
