package ru.pussy_penetrator.pchat.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Map;

public class AndroidHelpers {
    public static void changeActivity(AppCompatActivity activity, Class activityClass) {
        Intent intent = new Intent(activity, activityClass);
        activity.startActivity(intent);
    }

    public static void alert(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void alert(Context context, int messageResource) {
        Toast toast = Toast.makeText(context, context.getString(messageResource), Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void hideKeyboard(AppCompatActivity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboardDelayed(AppCompatActivity activity, Runnable runnable) {
        hideKeyboard(activity);

        new Handler().postDelayed(runnable, 100);
    }

    public static final int ANIMATION_TIME = 500;

    public static void toggleViews(final View hideView, final View showView) {
        toggleView(hideView, true);
        toggleView(showView, false);
    }

    private static void toggleView(final View view, final boolean show) {
        view.animate()
                .setDuration(ANIMATION_TIME)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
    }
}
