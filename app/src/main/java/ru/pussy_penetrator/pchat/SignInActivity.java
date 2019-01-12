package ru.pussy_penetrator.pchat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world", "a@a:aaaaaa"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignInTask mAuthTask = null;

    // UI references.
    private EditText mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = findViewById(R.id.sign_in_login);

        mPasswordView = findViewById(R.id.sign_in_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mFormView = findViewById(R.id.sign_in_form);
        mProgressView = findViewById(R.id.sign_in_progress);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passwordErrorMessage = validatePassword(password);
        if (passwordErrorMessage != null) {
            cancel = true;
            focusView = mPasswordView;
            mPasswordView.setError(passwordErrorMessage);
        }

        // Check for a valid login.
        String loginErrorMessage = validateLogin(login);
        if (loginErrorMessage != null) {
            cancel = true;
            focusView = mLoginView;
            mLoginView.setError(loginErrorMessage);
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            hideKeyboard();
            toggleProgress(true);
            mAuthTask = new UserSignInTask(login, password);
            mAuthTask.execute((Void) null);
        }
    }

    @Nullable
    private String validateLogin(String login) {
        if (login.isEmpty()) {
            return getString(R.string.error_field_required);
        }

        if (!login.matches("^[a-zA-Z0-9]+$")) {
            return getString(R.string.error_invalid_login_sign);
        }

        int minLength = getResources().getInteger(R.integer.login_min_length);
        int maxLength = getResources().getInteger(R.integer.login_max_length);
        if (login.length() < minLength || login.length() > maxLength) {
            return String.format(getString(R.string.error_invalid_login_length), minLength, maxLength);
        }

        char firstChar = login.charAt(0);
        if (!(firstChar >= 'a' && firstChar <= 'z' || firstChar >= 'A' && firstChar <= 'Z')) {
            return getString(R.string.error_invalid_login_first_letter);
        }

        return null;
    }

    @Nullable
    private String validatePassword(String password) {
        if (password.isEmpty()) {
            return getString(R.string.error_field_required);
        }

        if (!password.matches("^[a-zA-Z0-9_-]+$")) {
            return getString(R.string.error_invalid_password_sign);
        }

        int minLength = getResources().getInteger(R.integer.password_min_length);
        int maxLength = getResources().getInteger(R.integer.password_max_length);
        if (password.length() < minLength || password.length() > maxLength) {
            return String.format(getString(R.string.error_invalid_password_length), minLength, maxLength);
        }

        return null;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void toggleProgress(final boolean show) {
        // Fade-in the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

        mFormView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFormView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mProgressView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserSignInTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserSignInTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            toggleProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_credentials));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            toggleProgress(false);
        }
    }
}

