package com.example.uberclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.uberclone.R;
import com.example.uberclone.activities.client.RegisterActivity;

public class SelectOptionAuthActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button mButtonGoToLogin;
    Button mButtonGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Seleccionar Opción");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButtonGoToRegister = findViewById(R.id.btnGoToRegister);
        mButtonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        mButtonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, loginActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
