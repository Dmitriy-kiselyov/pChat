package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListPreviewResponse implements BaseResponse {
    private StatusResponse status;
    private List<UserPreview> mUserPreviews;

    public UserListPreviewResponse() {}

    private UserListPreviewResponse(List<UserPreview> userPreviews) {
        status = StatusResponse.SUCCESS;
        mUserPreviews = userPreviews;
    }

    public List<UserPreview> getUserPreviews() {
        return mUserPreviews;
    }

    @Override
    public StatusResponse getStatus() {
        return status;
    }

    @Nullable
    @Override
    public UserListPreviewResponse fromJSON(JSONObject json) {
        try {
            StatusResponse status = StatusResponse.from(json.getString("status"));
            if (status != StatusResponse.SUCCESS) {
                return null;
            }

            JSONArray jsonUsers = json.getJSONArray("response");
            ArrayList<UserPreview> users = new ArrayList<>();

            for (int i = 0; i < jsonUsers.length(); i++) {
                JSONObject jsonUser = jsonUsers.getJSONObject(i);
                String login = jsonUser.getString("login");
                UserPreview user = new UserPreview(login);
                users.add(user);
            }

            return new UserListPreviewResponse(users);
        } catch (JSONException e) {}

        return null;
    }
}
