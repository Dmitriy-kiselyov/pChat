package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorResponse {
    private int code;
    private String message;

    private ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Nullable
    public static ErrorResponse fromJSON(JSONObject json) {
        try {
            int code = json.getInt("code");
            String message = json.getString("message");

            return new ErrorResponse(code, message);
        } catch (JSONException e) {
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
