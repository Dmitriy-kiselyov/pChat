package ru.pussy_penetrator.pchat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import ru.pussy_penetrator.pchat.request.AuthResponse;
import ru.pussy_penetrator.pchat.request.AuthUserRequest;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.utils.AuthUtils;
import ru.pussy_penetrator.pchat.utils.RequestUtils;

public class SignInActivity extends AppCompatActivity {
    private static final int ANIMATION_TIME = 500;
    private static final int KEYBOARD_HIDE_TIME = 100;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mAuthRequest;

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

        mRequestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid login, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthRequest != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String login = mLoginView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passwordErrorMessage = AuthUtils.validatePassword(this, password);
        if (passwordErrorMessage != null) {
            cancel = true;
            focusView = mPasswordView;
            mPasswordView.setError(passwordErrorMessage);
        }

        // Check for a valid login.
        String loginErrorMessage = AuthUtils.validateLogin(this, login);
        if (loginErrorMessage != null) {
            cancel = true;
            focusView = mLoginView;
            mLoginView.setError(loginErrorMessage);
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            hideKeyboard();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleProgress(true);
                    makeAuthRequest(login, password);
                }
            }, KEYBOARD_HIDE_TIME);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void toggleProgress(final boolean show) {
        // Fade-in the progress spinner.
        mFormView.animate()
                .setDuration(ANIMATION_TIME)
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
                .setDuration(ANIMATION_TIME)
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void makeAuthRequest(String login, String password) {
        AuthUserRequest user = new AuthUserRequest(login, password);

        final SignInActivity _this = this;

        mAuthRequest = RequestUtils.requestSignIn(
                user,
                new ResponseCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        String token = response.getToken();
                        Preferences.get(_this).saveToken(token);

                        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFail(AuthResponse response) {
                        AuthResponse.ErrorCode errorCode = response.getErrorCode();

                        EditText focusView = null;
                        String message = null;

                        if (errorCode == AuthResponse.ErrorCode.LOGIN_VALIDATION) {
                            focusView = mLoginView;
                        }
                        if (errorCode == AuthResponse.ErrorCode.PASSWORD_VALIDATION) {
                            focusView = mPasswordView;
                        }
                        if (errorCode == AuthResponse.ErrorCode.INCORRECT_CREDENTIALS) {
                            focusView = mPasswordView;
                            message = getString(R.string.error_incorrect_credentials);
                        }

                        if (message == null) {
                            message = response.getErrorMessage();
                        }

                        if (focusView == null) {
                            alert(message);
                        } else {
                            final EditText finalFocusView = focusView;
                            final String finalMessage = message;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finalFocusView.setError(finalMessage);
                                    finalFocusView.requestFocus();
                                }
                            }, ANIMATION_TIME);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        alert(R.string.error_server);
                    }

                    @Override
                    public void onFinal() {
                        toggleProgress(false);
                        mAuthRequest = null;
                    }
                }
        );

        mRequestQueue.add(mAuthRequest);
    }

    private void alert(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void alert(int messageResource) {
        Toast toast = Toast.makeText(getApplicationContext(), getString(messageResource), Toast.LENGTH_SHORT);
        toast.show();
    }
}

