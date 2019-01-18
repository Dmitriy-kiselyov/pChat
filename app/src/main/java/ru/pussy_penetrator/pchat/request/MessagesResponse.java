package ru.pussy_penetrator.pchat.request;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesResponse implements BaseResponse {
    private StatusResponse status;
    private List<Message> mMessages;

    public MessagesResponse() {}

    private MessagesResponse(List<Message> messages) {
        status = StatusResponse.SUCCESS;
        mMessages = messages;
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    @Override
    public StatusResponse getStatus() {
        return status;
    }

    @Nullable
    @Override
    public MessagesResponse fromJSON(JSONObject json) {
        try {
            StatusResponse status = StatusResponse.from(json.getString("status"));
            if (status != StatusResponse.SUCCESS) {
                return null;
            }

            JSONArray jsonMessages = json.getJSONArray("response");
            ArrayList<Message> messages = new ArrayList<>();

            for (int i = 0; i < jsonMessages.length(); i++) {
                JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                int id = jsonMessage.getInt("id");
                long time = jsonMessage.getLong("time");
                String text = jsonMessage.getString("text");
                String sender = jsonMessage.getString("sender");

                Message message = new Message(id, time, text, sender);
                messages.add(message);
            }

            return new MessagesResponse(messages);
        } catch (JSONException e) {
            return null;
        }

    }
}
