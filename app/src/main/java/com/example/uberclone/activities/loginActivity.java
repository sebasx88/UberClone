package com.example.uberclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uberclone.R;
import com.example.uberclone.activities.client.MapClientActivity;
import com.example.uberclone.activities.client.RegisterActivity;
import com.example.uberclone.activities.driver.MapDriverActivity;
import com.example.uberclone.activities.driver.RegisterDriverActivity;
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
    SharedPreferences mPref;

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
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
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
                            String user = mPref.getString("user", "");
                            if (user.equals("client")){
                                //Toast.makeText(loginActivity.this, "El login se realizo exitosamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, MapClientActivity.class);
                                //Con el Flags evitamos que el conductor se pueda devolver al formulario de la registro
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                //Toast.makeText(loginActivity.this, "El email o password son incorrectos", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, MapDriverActivity.class);
                                //Con el Flags evitamos que el conductor se pueda devolver al formulario de la registro
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                        else{
                            Toast.makeText(loginActivity.this, "La contraseña o password son incorrectos", Toast.LENGTH_SHORT).show();
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

