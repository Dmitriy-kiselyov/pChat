package ru.pussy_penetrator.pchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.List;

import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.UserListPreviewResponse;
import ru.pussy_penetrator.pchat.request.UserPreview;
import ru.pussy_penetrator.pchat.utils.AndroidHelpers;
import ru.pussy_penetrator.pchat.utils.RequestUtils;

public class UserListActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mUserListRequest;

    private RecyclerView mUserRecyclerView;
    private ProgressBar mProgress;

    private AppCompatActivity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        _this = this;

        mUserRecyclerView = findViewById(R.id.user_list);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mUserRecyclerView.getContext(), 1);
        mUserRecyclerView.addItemDecoration(dividerItemDecoration);

        mProgress = findViewById(R.id.progress);

        mRequestQueue = Volley.newRequestQueue(this);
        makeRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_logout:
                logout();
                return true;
        }

        return false;
    }

    private void logout() {
        Preferences.get(this).clearToken();
        AndroidHelpers.changeActivity(this, SignInActivity.class);
    }

    private void makeRequest() {
        if (mUserListRequest != null) {
            return;
        }

        mUserListRequest = RequestUtils.requestUserList(getApplicationContext(), new ResponseCallback<UserListPreviewResponse>() {
            @Override
            public void onSuccess(UserListPreviewResponse response) {
                if (response != null) {
                    mUserRecyclerView.setAdapter(new UserPreviewAdapter(response.getUserPreviews()));
                }
            }

            @Override
            public void onFail(UserListPreviewResponse response) {
                AndroidHelpers.alert(getApplicationContext(), "Ошибка загрузки пользователей");
            }

            @Override
            public void onError(VolleyError error) {
                String alertMessage;
                if (error.networkResponse == null) {
                    alertMessage = getString(R.string.error_server);
                } else {
                    int code = error.networkResponse.statusCode;
                    String message;
                    try {
                        message = new String(error.networkResponse.data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        message = getString(R.string.error_server);
                    }

                    alertMessage = code + ": " + message;
                }

                AndroidHelpers.alert(getApplicationContext(), alertMessage);
            }

            @Override
            public void onFinal() {
                AndroidHelpers.toggleViews(mProgress, mUserRecyclerView);
                mUserListRequest = null;
            }
        });

        mRequestQueue.add(mUserListRequest);
    }

    private class UserPreviewHolder extends RecyclerView.ViewHolder {
        private TextView mLoginView;

        UserPreviewHolder(View itemView) {
            super(itemView);

            mLoginView = itemView.findViewById(R.id.list_user_login);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AndroidHelpers.changeActivity(_this, MessagesActivity.class);
                }
            });
        }

        public void bind(UserPreview user) {
            mLoginView.setText(user.getLogin());
        }
    }

    private class UserPreviewAdapter extends RecyclerView.Adapter<UserPreviewHolder> {

        private List<UserPreview> mUserMessagePreviews;

        public UserPreviewAdapter(List<UserPreview> userMessagePreviews) {
            mUserMessagePreviews = userMessagePreviews;
        }

        @Override
        public UserPreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = getLayoutInflater();
            View view = li.inflate(R.layout.list_user_message_preview, parent, false);

            return new UserPreviewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserPreviewHolder holder, int position) {
            UserPreview userMessagePreview = mUserMessagePreviews.get(position);
            holder.bind(userMessagePreview);
        }

        @Override
        public int getItemCount() {
            return mUserMessagePreviews.size();
        }
    }
}
