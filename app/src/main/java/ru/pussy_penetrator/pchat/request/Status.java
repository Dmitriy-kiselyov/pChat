package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

public enum Status {
    SUCCESS("success"), ERROR("error");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    String get() {
        return this.status;
    }

    @Nullable
    static Status from(String status) {
        for(Status response : Status.values()) {
            if (response.status.equals(status)) {
                return response;
            }
        }

        return null;
    }
}
