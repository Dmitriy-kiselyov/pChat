package ru.pussy_penetrator.pchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.pussy_penetrator.pchat.utils.AndroidHelpers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        //Preferences.get(this).clearToken(); // TODO: убрать, когда будет кнопка выхода

        String token = Preferences.get(this).getToken();
        if (token != null) {
            AndroidHelpers.changeActivity(this, UserListActivity.class);
        } else {
            AndroidHelpers.changeActivity(this, SignInActivity.class);
        }
    }
}
