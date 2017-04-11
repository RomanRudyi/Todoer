package com.android.example.todoer.data.retrofit.authorization.refresh;

import retrofit2.Call;
import retrofit2.http.POST;

public interface RefreshTokenService {

    //// TODO: 23.11.16 fix
    @POST("refresh_token")
    Call<TokenResponseModel> getNewToken();
}
