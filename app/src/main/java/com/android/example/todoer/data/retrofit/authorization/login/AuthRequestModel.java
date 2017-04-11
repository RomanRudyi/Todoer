package com.android.example.todoer.data.retrofit.authorization.login;

/**
 * Created by bohdan on 23.11.16.
 */
public class AuthRequestModel {
    private String serverAuthCode;

    public AuthRequestModel(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }

    public String getServerAuthCode() {
        return serverAuthCode;
    }

    public void setServerAuthCode(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }
}
