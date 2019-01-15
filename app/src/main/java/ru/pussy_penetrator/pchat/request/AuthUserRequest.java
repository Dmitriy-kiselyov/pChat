package ru.pussy_penetrator.pchat.request;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthUserRequest implements BaseRequest {
    private String login;
    private String password;

    public AuthUserRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("login", login);
            json.put("password", password);
        } catch (JSONException e) {}

        return json;
    }
}
