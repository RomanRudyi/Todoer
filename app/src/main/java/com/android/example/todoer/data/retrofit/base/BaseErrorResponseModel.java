package com.android.example.todoer.data.retrofit.base;


public class BaseErrorResponseModel {

    String message;
    int status_code;

    public BaseErrorResponseModel(String message, int status_code) {
        this.message = message;
        this.status_code = status_code;
    }

    public BaseErrorResponseModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    @Override
    public String toString() {
        return "BaseErrorResponseModel{" +
                "message='" + message + '\'' +
                ", status_code=" + status_code +
                '}';
    }
}
