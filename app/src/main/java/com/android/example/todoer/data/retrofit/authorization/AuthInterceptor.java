package com.android.example.todoer.data.retrofit.authorization;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bohdan.dailyplanner.data.PrefsManager;
import com.example.bohdan.dailyplanner.data.retrofit.authorization.refresh.RefreshTokenService;
import com.example.bohdan.dailyplanner.data.retrofit.authorization.refresh.TokenResponseModel;
import com.example.bohdan.dailyplanner.data.retrofit.base.ServiceGenerator;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthInterceptor implements Interceptor {

    private static final String TAG = AuthInterceptor.class.getSimpleName();
    private static final int MAX_REFRESH_REQUEST_NUMBER = 3;
    private static final int ACCESS_TOKEN_ERROR_CODE = 401;

    private PrefsManager preferencesManager;
    private int refreshTokenRequestsCount = 0;

    public AuthInterceptor(PrefsManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String accessToken = preferencesManager.getCurrentUserToken();
        Log.d(TAG, "build request");
        Request request = buidRequest(original, accessToken);
        // TODO for test purposes, delete later
//        if (refreshTokenRequestsCount == 0) {
//            request = buidRequest(original, "testToken");
//        } else {
//            request = buidRequest(original, accessToken);
//        }
        // try the request
        Response response = chain.proceed(request);
        if (response.code() == ACCESS_TOKEN_ERROR_CODE) {
            String newToken = getNewToken();
            Log.d(TAG, "auth error");
            if (!newToken.equals("") && refreshTokenRequestsCount < MAX_REFRESH_REQUEST_NUMBER) {
                // create a new request and modify it accordingly using the new token
                Request newRequest = buidRequest(request, newToken);
                // retry the request
                return chain.proceed(newRequest);
            }
        }
        // otherwise just pass the original response on
        return response;
    }

    @NonNull
    private String getNewToken() {
        try {
            TokenResponseModel authTokenModel = getNewAccessToken();
            if (authTokenModel != null) {
                return authTokenModel.getToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Request buidRequest(Request original, String accessToken) {
        Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", String.format("Bearer %s", accessToken))
                .method(original.method(), original.body());
        return requestBuilder.build();
    }

    private TokenResponseModel getNewAccessToken() throws IOException {
        String refreshToken = preferencesManager.getCurrentUserToken();
        if (refreshToken != null && !refreshToken.equals("")) {
            retrofit2.Response<TokenResponseModel> refreshTokenResponse = getAuthTokenModelResponse();
            if (refreshTokenResponse != null && refreshTokenResponse.isSuccessful()) {
                TokenResponseModel authTokenModel = refreshTokenResponse.body();
                preferencesManager.saveCurrentUserToken(authTokenModel.getToken());
                return authTokenModel;
            }
        }
        return null;
    }

    private retrofit2.Response<TokenResponseModel> getAuthTokenModelResponse() throws IOException {
        refreshTokenRequestsCount++;
        RefreshTokenService client = ServiceGenerator.createCallService(RefreshTokenService.class, preferencesManager);
        return client.getNewToken().execute();
    }
}
