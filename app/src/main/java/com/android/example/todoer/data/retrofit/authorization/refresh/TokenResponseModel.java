package com.android.example.todoer.data.retrofit.authorization.refresh;


public class TokenResponseModel {

    private String token;

    public TokenResponseModel(String token) {
        this.token = token;
    }

    public TokenResponseModel() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "TokenResponseModel{" +
                "token='" + token + '\'' +
                '}';
    }
}
