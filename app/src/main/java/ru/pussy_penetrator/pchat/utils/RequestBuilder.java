package ru.pussy_penetrator.pchat.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.pussy_penetrator.pchat.Preferences;
import ru.pussy_penetrator.pchat.request.BaseRequest;
import ru.pussy_penetrator.pchat.request.BaseResponse;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.Status;

public class RequestBuilder<Resp extends BaseResponse> {
    private Context mContext;

    public RequestBuilder() {
        super();
    }

    public RequestBuilder(Context context) {
        this();
        mContext = context;
    }

    public JsonObjectRequest build(
            int method,
            String url,
            @Nullable BaseRequest request,
            final Resp responseExample,
            final ResponseCallback<Resp> callback
    ) {
        JSONObject jsonRequest = request != null ? request.toJSON() : null;

        return new JsonObjectRequest(
                method,
                url,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        //System.out.println("Received response: " + json);

                        Resp response = (Resp) responseExample.fromJSON(json);

                        if (response.getStatus() == Status.SUCCESS) {
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
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();

                if (mContext == null) {
                    return headers;
                }

                String token = Preferences.get(mContext).getToken();
                if (token == null) {
                    return headers;
                }

                headers.put("Authorization", token);
                return headers;
            }
        };
    }
}
