package ru.pussy_penetrator.pchat.request;

import com.android.volley.VolleyError;

public interface ResponseCallback<Resp extends BaseResponse> {
    void onSuccess(Resp response);
    void onFail(Resp response);
    void onError(VolleyError error);
    void onFinal();
}
