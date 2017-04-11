package com.android.example.todoer.data;

import android.content.Context;

/**
 * Created by bohdan on 23.11.16.
 */

public class PrefsManager {

    private static PrefsManager prefsManager;

    private PrefsManager(Context context) {

    }

    public static PrefsManager getInstance(Context context){
        if (prefsManager == null)
            prefsManager = new PrefsManager(context);
        return prefsManager;
    }

    public String getCurrentUserToken() {
        return null;
    }

    public void saveCurrentUserToken(String token) {

    }
}
