package ru.pussy_penetrator.pchat.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import ru.pussy_penetrator.pchat.request.AuthResponse;
import ru.pussy_penetrator.pchat.request.AuthUserRequest;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.UserListPreviewResponse;

public class RequestUtils {
    private static String BASE_URL = "http://192.168.0.104:8080/rest/";
    private static String AUTH_URL = BASE_URL + "auth";
    private static String USERS_URL = BASE_URL + "users";

    public static JsonObjectRequest requestSignIn(AuthUserRequest user, ResponseCallback<AuthResponse> callback) {
        return new RequestBuilder<AuthResponse>().build(
                Request.Method.POST,
                AUTH_URL,
                user,
                new AuthResponse(),
                callback
        );
    }

    public static JsonObjectRequest requestSignUp(AuthUserRequest user, ResponseCallback<AuthResponse> callback) {
        return new RequestBuilder<AuthResponse>().build(
                Request.Method.PUT,
                AUTH_URL,
                user,
                new AuthResponse(),
                callback
        );
    }

    public static JsonObjectRequest requestUserList(Context context, ResponseCallback<UserListPreviewResponse> callback) {
        return new RequestBuilder<UserListPreviewResponse>(context).build(
                Request.Method.GET,
                USERS_URL,
                null,
                new UserListPreviewResponse(),
                callback
        );
    }
}
