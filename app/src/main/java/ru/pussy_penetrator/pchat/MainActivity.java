package ru.pussy_penetrator.pchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Preferences.get(this).clearToken(); // TODO: убрать, когда будет кнопка выхода

        String token = Preferences.get(this).getToken();
        if (token != null) {
            changeActivity(UserListActivity.class);
        } else {
            changeActivity(SignInActivity.class);
        }
    }

    private void changeActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
