package com.android.example.todoer.data.retrofit.base;

public class BaseModelParam<T> {
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponseModel{" +
                "data=" + data +
                '}';
    }
}
