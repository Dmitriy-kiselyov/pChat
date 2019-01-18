package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthResponse implements BaseResponse {
    private Status status;
    private ErrorResponse error;
    private String token;

    public AuthResponse() {}

    private AuthResponse(Status status, String token) {
        this.status = status;
        this.token = token;
    }

    private AuthResponse(Status status, ErrorResponse error) {
        this.status = status;
        this.error = error;
    }

    public AuthResponse fromJSON(JSONObject json) {
        try {
            Status status = Status.from(json.getString("status"));

            if (status == Status.SUCCESS) {
                String token = json.getString("token");
                return new AuthResponse(status, token);
            }

            if (status == Status.ERROR) {
                ErrorResponse error = ErrorResponse.fromJSON(json.getJSONObject("error"));
                return new AuthResponse(status, error);
            }
        } catch (JSONException e) {}

        return null;
    }

    public Status getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return error != null ? ErrorCode.from(error.getCode()) : null;
    }

    public String getErrorMessage() {
        return error != null ? error.getMessage() : null;
    }

    public String getToken() {
        return token;
    }

    public enum ErrorCode {
        INCORRECT_CREDENTIALS(100),
        LOGIN_VALIDATION(101),
        PASSWORD_VALIDATION(102),
        USER_EXISTS(103),
        DATABASE_ERROR(110);

        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        int get() {
            return code;
        }

        @Nullable
        static ErrorCode from(int code) {
            for(ErrorCode errorCode : ErrorCode.values()) {
                if (errorCode.code == code) {
                    return errorCode;
                }
            }

            return null;
        }
    }
}
