package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public interface BaseResponse {
    StatusResponse getStatus();
    @Nullable
    BaseResponse fromJSON(JSONObject json);
}
