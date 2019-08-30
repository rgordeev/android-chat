package ru.rgordeev.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import ru.rgordeev.chat.data.Message;
import ru.rgordeev.chat.components.MessageAdapter;
import ru.rgordeev.chat.R;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatActivity extends AppCompatActivity {

    private static final String WS_URL = "ws://192.168.78.116:8080/chat/android";
    private static final String recipient = "Server";

    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private EditText editText;
    private CompositeDisposable compositeDisposable;
    private StompClient stompClient;
    private Gson jsonParser;
    private String login;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesView = findViewById(R.id.messages_view);
        messageAdapter = new MessageAdapter(this);
        messagesView.setAdapter(messageAdapter);
        editText = findViewById(R.id.editText);

        jsonParser = new Gson();

        Intent intent = this.getIntent();
        login = intent.getStringExtra("login");
        token = intent.getStringExtra("token");

        stompConnection();
    }

    private void stompConnection() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);
        resetSubscriptions();

        compositeDisposable.add(buildLifecycle(stompClient));
        compositeDisposable.add(
                stompClient.topic("/topic/activity")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(topicMessage -> {
                            Log.d("", "Received " + topicMessage.getPayload());
                            outputMessages(jsonParser.fromJson(topicMessage.getPayload(), Message[].class));
                        }, throwable -> {
                            Log.e("", "Error on subscribe topic", throwable);
                            backToPreviousForm();
                        })
        );
        compositeDisposable.add(
                stompClient.send("/app/history",
                        "{\"token\":\"" + token + "\"}")
                        .compose(applySchedulers())
                        .subscribe(() -> {
                            Log.d("", "STOMP echo send successfully");
                        }, throwable -> {
                            Log.e("", "Error send STOMP echo", throwable);
                            toast(throwable.getMessage());
                        })
        );

        stompClient.connect();
    }

    private Disposable buildLifecycle(StompClient stompClient) {
        return stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e("", "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            backToPreviousForm();
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            toast("Stomp failed server heartbeat");
                            backToPreviousForm();
                            break;
                    }
                });
    }

    public void onClickSendMessage(View view) {
        String msg = editText.getText().toString();
        editText.getText().clear();

        if (msg.length() > 0) {
            sendMessage(new Message(login, recipient, msg, true));
        }
    }

    private void sendMessage(Message message) {
        compositeDisposable.add(
                stompClient.send("/app/send_message",
                        jsonParser.toJson(message, Message.class))
                        .compose(applySchedulers())
                        .subscribe(() -> {
                            Log.d("", "STOMP echo send successfully");
                            showMessage(message);
                        }, throwable -> {
                            Log.e("", "Error send STOMP echo", throwable);
                            toast(throwable.getMessage());
                        })
        );
    }

    private void outputMessages(Message... messages) {
        runOnUiThread(() -> {
            for (Message msg : messages) {
                msg.checkIsBelongsToCurrentUser(login);
                showMessage(msg);
            }
        });
    }

    private void showMessage(Message message) {
        messageAdapter.add(message);
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void backToPreviousForm() {
        finish();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void toast(String text) {
        Log.i("", text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        stompClient.disconnect();
        if (compositeDisposable != null)
            compositeDisposable.dispose();
        super.onDestroy();
    }

}
