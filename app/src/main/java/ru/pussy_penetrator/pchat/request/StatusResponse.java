package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class StatusResponse implements BaseResponse {
    private Status status;
    private ErrorResponse error;

    public StatusResponse() {}

    private StatusResponse(Status status) {
        this.status = status;
    }

    private StatusResponse(ErrorResponse error) {
        this(Status.ERROR);
        this.error = error;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Nullable
    @Override
    public StatusResponse fromJSON(JSONObject json) {
        try {
            Status status = Status.from(json.getString("status"));

            if (status == Status.SUCCESS) {
                return new StatusResponse(status);
            }
            if (status == Status.ERROR) {
                ErrorResponse error = ErrorResponse.fromJSON(json.getJSONObject("error"));
                return new StatusResponse(error);
            }
        }
        catch (JSONException e) {}

        return null;
    }
}
