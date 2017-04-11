package com.android.example.todoer.data.retrofit.base;

import android.support.annotation.NonNull;
import com.android.example.todoer.data.PrefsManager;
import com.android.example.todoer.data.retrofit.authorization.AuthInterceptor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String BASE_URL = "https://microsoft-apiapp07b098e897ec4118ba82fbd565fb0e19.azurewebsites.net/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder();

    public static <S> S createService(final Class<S> serviceClass) {
        addLoggingInterceptor();
        Retrofit retrofit = getBaseBuilder()
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(final Class<S> serviceClass, PrefsManager prefsManager) {
        addInterceptors(prefsManager);
        Retrofit retrofit = getBaseBuilder()
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createCallService(final Class<S> serviceClass, PrefsManager prefsManager) {
        String token = prefsManager.getCurrentUserToken();
        builder =
                new Retrofit.Builder();
        httpClient.interceptors().add(chain -> {
            Request request = addHeadersToRequest(token, chain);
            return chain.proceed(request);
        });
        Retrofit retrofit = getBaseBuilder()
                .build();
        return retrofit.create(serviceClass);
    }

    @NonNull
    private static Retrofit.Builder getBaseBuilder() {
        return builder.client(httpClient.build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    private static Request addHeadersToRequest(String token, Interceptor.Chain chain) {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", String.format("%s", token))
                .method(original.method(), original.body());

        return requestBuilder.build();
    }

    private static void addInterceptors(PrefsManager preferencesManager) {
        httpClient.interceptors().clear();
        addLoggingInterceptor();
        addBaseInterceptor(preferencesManager);
    }

    private static void addLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
    }

    private static void addBaseInterceptor(PrefsManager preferencesManager) {
        AuthInterceptor authInterceptor = new AuthInterceptor(preferencesManager);
        httpClient.interceptors().add(authInterceptor);
    }
}
