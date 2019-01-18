package ru.pussy_penetrator.pchat.request;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageRequest implements BaseRequest  {
    private String text;
    private String target;

    public MessageRequest(String text, String target) {
        this.text = text;
        this.target = target;
    }

    public String getText() {
        return text;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("text", text);
            json.put("target", target);
        } catch (JSONException e) {}

        return json;
    }
}
