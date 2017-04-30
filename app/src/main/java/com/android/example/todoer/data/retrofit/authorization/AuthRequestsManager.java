package com.android.example.todoer.data.retrofit.authorization;

import android.support.annotation.NonNull;

import com.android.example.todoer.data.PrefsManager;
import com.android.example.todoer.data.retrofit.authorization.login.AuthRequestModel;
import com.android.example.todoer.data.retrofit.authorization.login.AuthResponseModel;
import com.android.example.todoer.data.retrofit.authorization.login.AuthService;
import com.android.example.todoer.data.retrofit.base.ServiceGenerator;

import rx.Observable;

/**
 * Created by bohdan on 23.11.16.
 */

public class AuthRequestsManager {

    private PrefsManager prefsManager;

    public AuthRequestsManager(@NonNull PrefsManager prefsManager) {
        this.prefsManager = prefsManager;
    }

    public Observable<AuthResponseModel> authUser(AuthRequestModel authRequestModel) {
        return ServiceGenerator.createService(AuthService.class, prefsManager)
                .auth(authRequestModel);
    }
}
