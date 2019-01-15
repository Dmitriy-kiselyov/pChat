package ru.pussy_penetrator.pchat.utils;

import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ru.pussy_penetrator.pchat.request.BaseRequest;
import ru.pussy_penetrator.pchat.request.BaseResponse;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.StatusResponse;

public class RequestBuilder<Resp extends BaseResponse> {
    public JsonObjectRequest build(
            int method,
            String url,
            @Nullable BaseRequest request,
            final Resp responseExample,
            final ResponseCallback<Resp> callback
    ) {
        return new JsonObjectRequest(
                method,
                url,
                request.toJSON(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        System.out.println("Received response: " + json);

                        Resp response = (Resp) responseExample.fromJSON(json);

                        if (response.getStatus() == StatusResponse.SUCCESS) {
                            callback.onSuccess(response);
                        } else {
                            callback.onFail(response);
                        }

                        callback.onFinal();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                        callback.onFinal();
                    }
                }
        );
    }
}
