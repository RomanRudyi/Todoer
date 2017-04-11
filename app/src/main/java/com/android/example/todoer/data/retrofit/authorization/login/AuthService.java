package com.android.example.todoer.data.retrofit.authorization.login;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface AuthService {

    @POST("api/Auth/Login")
    Observable<AuthResponseModel> auth(@Body() AuthRequestModel model);
}
