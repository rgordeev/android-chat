package ru.rgordeev.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.rgordeev.chat.R;

public class ChatActivity extends AppCompatActivity {

    private FragmentManager manager;
    private String login;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        manager = getSupportFragmentManager();

        Intent intent = this.getIntent();
        login = intent.getStringExtra("login");
        token = intent.getStringExtra("token");

        ChatFragment chatFragment = new ChatFragment();

        Bundle bundleCompat = new Bundle();
        bundleCompat.putString("login", login);
        bundleCompat.putString("token", token);

        chatFragment.setArguments(bundleCompat);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, chatFragment, "chatFragment");
        transaction.commit();
    }

}
