package com.example.uberclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uberclone.R;
import com.example.uberclone.include.MyToolbar;
import com.example.uberclone.providers.AuthProvider;
import com.example.uberclone.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import dmax.dialog.SpotsDialog;

public class loginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText getmTextInputPassword;
    Button mButtonLogin;

    AuthProvider mAuthProvider;
    ClientProvider mClientProvider;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyToolbar.show(this, "Login de Usuario", true);


        mTextInputEmail = findViewById(R.id.textInputEmail);
        getmTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();

        mDialog = new SpotsDialog.Builder().setContext(loginActivity.this).setMessage("Espere un momento").build();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {

        String email = mTextInputEmail.getText().toString();
        String password = getmTextInputPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()){
            if (password.length() >=6 ){
                mDialog.show();
                mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(loginActivity.this, "El login se realizo exitosamente", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(loginActivity.this, "El email o password son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }
}

