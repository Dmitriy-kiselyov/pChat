package ru.pussy_penetrator.pchat.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.io.UnsupportedEncodingException;

import ru.pussy_penetrator.pchat.Preferences;
import ru.pussy_penetrator.pchat.R;
import ru.pussy_penetrator.pchat.request.AuthResponse;
import ru.pussy_penetrator.pchat.request.AuthUserRequest;
import ru.pussy_penetrator.pchat.request.MessageRequest;
import ru.pussy_penetrator.pchat.request.MessagesResponse;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.StatusResponse;
import ru.pussy_penetrator.pchat.request.UserListPreviewResponse;

public class RequestUtils {
    public static JsonObjectRequest requestSignIn(Context context, AuthUserRequest user, ResponseCallback<AuthResponse> callback) {
        return new RequestBuilder<AuthResponse>().build(
                Request.Method.POST,
                getAuthUrl(context),
                user,
                new AuthResponse(),
                callback
        );
    }

    public static JsonObjectRequest requestSignUp(Context context, AuthUserRequest user, ResponseCallback<AuthResponse> callback) {
        return new RequestBuilder<AuthResponse>().build(
                Request.Method.PUT,
                getAuthUrl(context),
                user,
                new AuthResponse(),
                callback
        );
    }

    public static JsonObjectRequest requestUserList(Context context, ResponseCallback<UserListPreviewResponse> callback) {
        return new RequestBuilder<UserListPreviewResponse>(context).build(
                Request.Method.GET,
                getUsersUrl(context),
                null,
                new UserListPreviewResponse(),
                callback
        );
    }

    public static JsonObjectRequest requestMessages(Context context, String login, int untilId, ResponseCallback<MessagesResponse> callback) {
        return new RequestBuilder<MessagesResponse>(context).build(
                Request.Method.GET,
                getMessageUrl(context, login, untilId),
                null,
                new MessagesResponse(),
                callback
        );
    }

    public static JsonObjectRequest sendMessage(Context context, MessageRequest message, ResponseCallback<StatusResponse> callback) {
        return new RequestBuilder<StatusResponse>(context).build(
                Request.Method.POST,
                getMessageUrl(context),
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

    private static String getBaseUrl(Context context) {
        return "http://" + Preferences.get(context).getHost() + "/rest/";
    }

    private static String getAuthUrl(Context context) {
        return getBaseUrl(context) + "auth";
    }

    private static String getUsersUrl(Context context) {
        return getBaseUrl(context) + "users";
    }

    private static String getMessageUrl(Context context) {
        return getBaseUrl(context) + "messages";
    }

    private static String getMessageUrl(Context context, String login, int untilId) {
        return getMessageUrl(context) + "?for=" + login + "&until=" + untilId;
    }
}
