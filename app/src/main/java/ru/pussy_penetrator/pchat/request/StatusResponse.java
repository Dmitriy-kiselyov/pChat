package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

public enum StatusResponse {
    SUCCESS("success"), ERROR("error");

    private final String status;

    StatusResponse(String status) {
        this.status = status;
    }

    String get() {
        return this.status;
    }

    @Nullable
    static StatusResponse from(String status) {
        for(StatusResponse response : StatusResponse.values()) {
            if (response.status.equals(status)) {
                return response;
            }
        }

        return null;
    }
}
