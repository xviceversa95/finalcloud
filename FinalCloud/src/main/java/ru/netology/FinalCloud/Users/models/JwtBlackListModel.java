package ru.netology.FinalCloud.Users.models;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Indexed;

import java.util.Date;

@Document("jwt_blacklist")
public class JwtBlackListModel {

    private String token;
    @Id
    private String id;
    private Date expiresAt;

    public JwtBlackListModel(String token, String id, Date expiresAt) {
        this.token = token;
        this.id = id;
        this.expiresAt = expiresAt;
    }

    public JwtBlackListModel() {
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JwtBlackListModel{" + "token='" + token + '\'' + ", id='" + id + '\'' + ", expiresAt=" + expiresAt + '}';
    }
}
