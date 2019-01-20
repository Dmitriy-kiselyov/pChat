package ru.pussy_penetrator.pchat;

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

import java.util.LinkedList;
import java.util.List;

import ru.pussy_penetrator.pchat.request.Message;
import ru.pussy_penetrator.pchat.request.MessageRequest;
import ru.pussy_penetrator.pchat.request.MessagesResponse;
import ru.pussy_penetrator.pchat.request.ResponseCallback;
import ru.pussy_penetrator.pchat.request.StatusResponse;
import ru.pussy_penetrator.pchat.utils.AndroidHelpers;
import ru.pussy_penetrator.pchat.utils.RequestUtils;
import ru.pussy_penetrator.pchat.utils.SoundManager;

public class MessagesActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mPollMessagesRequest;
    private ResponseCallback<MessagesResponse> mPollMessagesCallback;
    private JsonObjectRequest mSendMessageRequest;
    private int mLastMessageId;
    private boolean mShouldPoll;

    private RecyclerView mMessagesRecyclerView;
    private TextView mEmptyMessagesTextView;
    private MessageAdapter mMessagesAdapter;
    private EditText mMessageEdit;
    private Button mSendButton;

    private String mSenderLogin;
    private List<Message> mMessages = new LinkedList<>();

    private SoundManager mSoundManager;

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

        mEmptyMessagesTextView = findViewById(R.id.empty_messages);

        mSendButton = findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSendRequest();
            }
        });

        mLastMessageId = 0;
        mRequestQueue = Volley.newRequestQueue(this);

        mSoundManager = new SoundManager(this);
        mSoundManager.load(R.raw.message_receive);
        mSoundManager.load(R.raw.message_send);
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

        if (mPollMessagesCallback == null) {
            mPollMessagesCallback = new ResponseCallback<MessagesResponse>() {
                private final int POLL_FREQUENCY = 100;
                private final int POLL_FREQUENCY_ERROR = 3000;

                private boolean mFirstMessagesReceived = false;
                private boolean mRequestEndedWithError = false;

                @Override
                public void onSuccess(MessagesResponse response) {
                    List<Message> newMessages = response.getMessages();
                    updateMessages(newMessages);

                    if (newMessages.isEmpty()) {
                        return;
                    }

                    mLastMessageId = newMessages.get(newMessages.size() - 1).getId();

                    playSoundIfNeeded(newMessages);
                    mFirstMessagesReceived = true;

                    mRequestEndedWithError = false;
                }

                private void playSoundIfNeeded(List<Message> messages) {
                    if (!mFirstMessagesReceived) {
                        return;
                    }

                    for(Message message : messages) {
                        if (message.getSender().equals(mSenderLogin)) {
                            mSoundManager.play(R.raw.message_receive);
                            return;
                        }
                    }
                }

                @Override
                public void onFail(MessagesResponse response) {
                    if (!mRequestEndedWithError) {
                        AndroidHelpers.alert(getApplicationContext(), "Ошибка загрузки сообщений");
                    }

                    mRequestEndedWithError = true;
                }

                @Override
                public void onError(VolleyError error) {
                    if (!mRequestEndedWithError) {
                        RequestUtils.alertError(getApplicationContext(), error);
                    }

                    mRequestEndedWithError = true;
                }

                @Override
                public void onFinal() {
                    mPollMessagesRequest = null;

                    int delay = mRequestEndedWithError ? POLL_FREQUENCY : POLL_FREQUENCY_ERROR;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            makePollRequest();
                        }
                    }, delay);
                }
            };
        }

        mPollMessagesRequest = RequestUtils.requestMessages(this, mSenderLogin, mLastMessageId + 1, mPollMessagesCallback);
        mRequestQueue.add(mPollMessagesRequest);
    }

    private void updateMessages(List<Message> newMessages) {
        mMessages.addAll(newMessages);

        if (mMessages.isEmpty()) {
            mEmptyMessagesTextView.setVisibility(View.VISIBLE);
            return;
        } else {
            mEmptyMessagesTextView.setVisibility(View.GONE);
            mMessagesRecyclerView.setVisibility(View.VISIBLE);
        }

        if (newMessages.isEmpty()) {
            return;
        }

        mMessagesAdapter.notifyDataSetChanged();
        mMessagesRecyclerView.scrollToPosition(mMessages.size() - 1);
    }

    private void makeSendRequest() {
        if (mSendMessageRequest != null) {
            return;
        }

        final String messageText = mMessageEdit.getText().toString();
        MessageRequest message = new MessageRequest(messageText, mSenderLogin);
        mMessageEdit.setText("");

        mSendMessageRequest = RequestUtils.sendMessage(this, message, new ResponseCallback<StatusResponse>() {
            @Override
            public void onSuccess(StatusResponse response) {
                mSoundManager.play(R.raw.message_send);
            }

            @Override
            public void onFail(StatusResponse response) {
                AndroidHelpers.alert(getApplicationContext(), "Не удалось отправить сообщение"); // TODO: подумать
                tryReturnMessage();
            }

            @Override
            public void onError(VolleyError error) {
                RequestUtils.alertError(getApplicationContext(), error); // TODO: подумать
                tryReturnMessage();
            }

            private void tryReturnMessage() {
                if (mMessageEdit.getText().toString().isEmpty()) {
                    mMessageEdit.setText(messageText);
                }
            }

            @Override
            public void onFinal() {
                mSendMessageRequest = null;
            }
        });

        mRequestQueue.add(mSendMessageRequest);
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
