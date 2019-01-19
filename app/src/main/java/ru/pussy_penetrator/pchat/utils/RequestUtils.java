package ru.pussy_penetrator.pchat.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import ru.pussy_penetrator.pchat.R;
import ru.pussy_penetrator.pchat.request.AuthResponse;
import ru.pussy_penetrator.pchat.request.AuthUserRequest;
import ru.pussy_penetrator.pchat.request.MessageRequest;
import ru.pussy_penetrator.pchat.request.MessagesResponse;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.StatusResponse;
import ru.pussy_penetrator.pchat.request.UserListPreviewResponse;

public class RequestUtils {
    private static String BASE_URL = "http://192.168.0.104:8080/rest/";
    private static String AUTH_URL = BASE_URL + "auth";
    private static String USERS_URL = BASE_URL + "users";
    private static String MESSAGES_URL = BASE_URL + "messages";

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

    public static JsonObjectRequest requestMessages(Context context, String login, int untilId, ResponseCallback<MessagesResponse> callback) {
        return new RequestBuilder<MessagesResponse>(context).build(
                Request.Method.GET,
                MESSAGES_URL + "?for=" + login + "&until=" + untilId,
                null,
                new MessagesResponse(),
                callback
        );
    }

    public static JsonObjectRequest sendMessage(Context context, MessageRequest message, ResponseCallback<StatusResponse> callback) {
        return new RequestBuilder<StatusResponse>(context).build(
                Request.Method.POST,
                MESSAGES_URL,
                message,
                new StatusResponse(),
                callback
        );
    }

    public static void alertError(Context context, VolleyError error) {
        String alertMessage;
        if (error.networkResponse == null) {
            alertMessage = context.getString(R.string.error_network);
        } else {
            int code = error.networkResponse.statusCode;

            String message;
            try {
                message = new String(error.networkResponse.data, "UTF-8");
                if (message.length() > 30) {
                    message = context.getString(R.string.error_server);
                }
            } catch (UnsupportedEncodingException e) {
                message = context.getString(R.string.error_server);
            }

            alertMessage = code + ": " + message;
        }

        AndroidHelpers.alert(context, alertMessage);
    }
}
