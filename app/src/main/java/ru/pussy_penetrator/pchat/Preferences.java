package ru.pussy_penetrator.pchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.Nullable;

public class Preferences {
    private static final String PREFERENCES = "pussy_settings";

    private static final String HOST_DEFAULT_VALUE = "192.168.0.104:8080";

    private static final String TOKEN = "token";
    private static final String HOST = "host";

    private static Preferences instance;

    private SharedPreferences mPrefs;

    private Preferences(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static Preferences get(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public void saveToken(String token) {
        save(TOKEN, token);
    }

    @Nullable
    public String getToken() {
        return mPrefs.getString(TOKEN, null);
    }

    public void clearToken() {
        clear(TOKEN);
    }

    public void saveHost(String host) {
        save(HOST, host);
    }

    public String getHost() {
        return mPrefs.getString(HOST, HOST_DEFAULT_VALUE);
    }

    private void clear(String key) {
        Editor editor = mPrefs.edit();
        editor.remove(key);
        editor.apply();
    }

    private void save(String key, String value) {
        Editor editor = mPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
