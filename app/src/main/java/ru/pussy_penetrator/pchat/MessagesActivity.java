package ru.pussy_penetrator.pchat;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import ru.pussy_penetrator.pchat.request.Message;
import ru.pussy_penetrator.pchat.request.MessageRequest;
import ru.pussy_penetrator.pchat.request.MessagesResponse;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.StatusResponse;
import ru.pussy_penetrator.pchat.utils.AndroidHelpers;
import ru.pussy_penetrator.pchat.utils.RequestUtils;

public class MessagesActivity extends AppCompatActivity {
    private final int POLL_FREQUENCY = 600;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mPollMessagesRequest;
    private JsonObjectRequest mSendMessageRequest;
    private int mLastMessageId;
    private boolean mShouldPoll;

    private RecyclerView mMessagesRecyclerView;
    private MessageAdapter mMessagesAdapter;
    private EditText mMessageEdit;
    private Button mSendButton;

    private String mSenderLogin;
    private List<Message> mMessages = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mSenderLogin = getIntent().getStringExtra("login");
        setTitle(mSenderLogin);

        mMessagesRecyclerView = findViewById(R.id.messages);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesAdapter = new MessageAdapter(mMessages);
        mMessagesRecyclerView.setAdapter(mMessagesAdapter);

        mMessageEdit = findViewById(R.id.message);

        mSendButton = findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSendRequest();
            }
        });

        mLastMessageId = 0;
        mRequestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mShouldPoll = true;
        makePollRequest();
    }

    @Override
    protected void onStop() {
        mShouldPoll = false;

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    private void makePollRequest() {
        if (mPollMessagesRequest != null || !mShouldPoll) {
            return;
        }

        mPollMessagesRequest = RequestUtils.requestMessages(this, mSenderLogin, mLastMessageId + 1, new ResponseCallback<MessagesResponse>() {
            @Override
            public void onSuccess(MessagesResponse response) {
                List<Message> newMessages = response.getMessages();
                if (newMessages.isEmpty()) {
                    return;
                }

                mMessages.addAll(newMessages);
                mMessagesAdapter.notifyDataSetChanged();

                mLastMessageId = newMessages.get(newMessages.size() - 1).getId();
            }

            @Override
            public void onFail(MessagesResponse response) {
                AndroidHelpers.alert(getApplicationContext(), "Ошибка загрузки сообщений");
            }

            @Override
            public void onError(VolleyError error) {
                onErrorRequest(error);
            }

            @Override
            public void onFinal() {
                mPollMessagesRequest = null;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makePollRequest();
                    }
                }, POLL_FREQUENCY);
            }
        });

        mRequestQueue.add(mPollMessagesRequest);
    }

    private void makeSendRequest() {
        if (mSendMessageRequest != null) {
            return;
        }

        MessageRequest message = new MessageRequest(mMessageEdit.getText().toString(), mSenderLogin);
        mMessageEdit.setText("");

        mSendMessageRequest = RequestUtils.sendMessage(this, message, new ResponseCallback<StatusResponse>() {
            @Override
            public void onSuccess(StatusResponse response) {
                AndroidHelpers.alert(getApplicationContext(), "Success"); // TODO: подумать
            }

            @Override
            public void onFail(StatusResponse response) {
                AndroidHelpers.alert(getApplicationContext(), "Fail"); // TODO: подумать
            }

            @Override
            public void onError(VolleyError error) {
                onErrorRequest(error);
            }

            @Override
            public void onFinal() {
                mSendMessageRequest = null;
            }
        });

        mRequestQueue.add(mSendMessageRequest);
    }

    private void onErrorRequest(VolleyError error) { // TODO: общая часть
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

    private class MessageHolder extends RecyclerView.ViewHolder {
        private TextView mTimeView;
        private TextView mTextView;

        MessageHolder(View itemView) {
            super(itemView);

            mTimeView = itemView.findViewById(R.id.time);
            mTextView = itemView.findViewById(R.id.text);
        }

        public void bind(Message message) {
            mTimeView.setText(message.getFormattedTime());
            mTextView.setText(message.getText());
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

        private static final int TYPE_SELF = 1;
        private static final int TYPE_ANOTHER = 2;

        private List<Message> mMessages;

        public MessageAdapter(List<Message> messages) {
            mMessages = messages;
        }

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = getLayoutInflater();

            int layout = 0;
            switch (viewType) {
                case TYPE_SELF:
                    layout = R.layout.list_message_from_self;
                    break;
                case TYPE_ANOTHER:
                    layout = R.layout.list_message_from_another;
            }

            View view = li.inflate(layout, parent, false);

            return new MessageHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            Message message = mMessages.get(position);

            if (message.getSender().equals(mSenderLogin)) {
                return TYPE_ANOTHER;
            } else {
                return TYPE_SELF;
            }
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            Message message = mMessages.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }
}
