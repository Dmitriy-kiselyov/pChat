package ru.pussy_penetrator.pchat.utils;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import ru.pussy_penetrator.pchat.request.AuthResponse;
import ru.pussy_penetrator.pchat.request.AuthUserRequest;
import ru.pussy_penetrator.pchat.request.ResponseCallback;

public class RequestUtils {
    private static String BASE_URL = "http://192.168.0.104:8080/rest/";
    private static String AUTH_URL = BASE_URL + "auth";

    public static JsonObjectRequest requestSignIn(AuthUserRequest user, ResponseCallback<AuthResponse> callback) {
        return new RequestBuilder<AuthResponse>().build(
                Request.Method.POST,
                RequestUtils.AUTH_URL,
                user,
                new AuthResponse(),
                callback
        );
    }

    public static JsonObjectRequest requestSignUp(AuthUserRequest user, ResponseCallback<AuthResponse> callback) {
        return new RequestBuilder<AuthResponse>().build(
                Request.Method.PUT,
                RequestUtils.AUTH_URL,
                user,
                new AuthResponse(),
                callback
        );
    }
}
