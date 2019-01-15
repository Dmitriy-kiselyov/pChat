package ru.pussy_penetrator.pchat.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import ru.pussy_penetrator.pchat.R;

public class AuthUtils {
    @Nullable
    public static String validateLogin(Context context, String login) {
        if (login.isEmpty()) {
            return context.getString(R.string.error_field_required);
        }

        if (!login.matches("^[a-zA-Z0-9]+$")) {
            return context.getString(R.string.error_invalid_login_sign);
        }

        int minLength = context.getResources().getInteger(R.integer.login_min_length);
        int maxLength = context.getResources().getInteger(R.integer.login_max_length);
        if (login.length() < minLength || login.length() > maxLength) {
            return String.format(context.getString(R.string.error_invalid_login_length), minLength, maxLength);
        }

        char firstChar = login.charAt(0);
        if (!(firstChar >= 'a' && firstChar <= 'z' || firstChar >= 'A' && firstChar <= 'Z')) {
            return context.getString(R.string.error_invalid_login_first_letter);
        }

        return null;
    }

    @Nullable
    public static String validatePassword(Context context, String password) {
        if (password.isEmpty()) {
            return context.getString(R.string.error_field_required);
        }

        if (!password.matches("^[a-zA-Z0-9_-]+$")) {
            return context.getString(R.string.error_invalid_password_sign);
        }

        int minLength = context.getResources().getInteger(R.integer.password_min_length);
        int maxLength = context.getResources().getInteger(R.integer.password_max_length);
        if (password.length() < minLength || password.length() > maxLength) {
            return String.format(context.getString(R.string.error_invalid_password_length), minLength, maxLength);
        }

        return null;
    }
}
