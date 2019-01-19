package ru.pussy_penetrator.pchat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import ru.pussy_penetrator.pchat.request.AuthResponse;
import ru.pussy_penetrator.pchat.request.AuthUserRequest;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.utils.AndroidHelpers;
import ru.pussy_penetrator.pchat.utils.AuthUtils;
import ru.pussy_penetrator.pchat.utils.RequestUtils;

import static ru.pussy_penetrator.pchat.utils.AndroidHelpers.ANIMATION_TIME;

public class SignInActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mAuthRequest;

    // UI references.
    private EditText mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mFormView;

    private AppCompatActivity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        _this = this;

        mLoginView = findViewById(R.id.auth_login);

        mPasswordView = findViewById(R.id.auth_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptSignIn();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = findViewById(R.id.auth_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });

        mFormView = findViewById(R.id.auth_form);
        mProgressView = findViewById(R.id.auth_progress);

        mRequestQueue = Volley.newRequestQueue(this);

        TextView signUpRedirect = findViewById(R.id.auth_redirect);
        signUpRedirect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidHelpers.changeActivity(_this, SignUpActivity.class);
            }
        });
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid login, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignIn() {
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
            AndroidHelpers.hideKeyboardDelayed(this, new Runnable() {
                @Override
                public void run() {
                    AndroidHelpers.toggleViews(mFormView, mProgressView);
                    makeAuthRequest(login, password);
                }
            });
        }
    }

    private void makeAuthRequest(String login, String password) {
        AuthUserRequest user = new AuthUserRequest(login, password);

        mAuthRequest = RequestUtils.requestSignIn(
                this,
                user,
                new ResponseCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        String token = response.getToken();
                        Preferences.get(getApplicationContext()).saveToken(token);

                        AndroidHelpers.changeActivity(_this, UserListActivity.class);
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
                            AndroidHelpers.alert(getApplicationContext(), message);
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

                        AndroidHelpers.toggleViews(mProgressView, mFormView);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        AndroidHelpers.toggleViews(mProgressView, mFormView);
                        AndroidHelpers.alert(getApplicationContext(), R.string.error_server);
                    }

                    @Override
                    public void onFinal() {
                        mAuthRequest = null;
                    }
                }
        );

        mRequestQueue.add(mAuthRequest);
    }
}

