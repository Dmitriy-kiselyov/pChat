package ru.pussy_penetrator.pchat.request;

public class UserPreview {
    private String login;

    public UserPreview(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
