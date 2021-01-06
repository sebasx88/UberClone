package com.example.uberclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.uberclone.R;
import com.example.uberclone.activities.client.MapClientActivity;
import com.example.uberclone.activities.driver.MapDriverActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button mButtonIAmClient;
    Button mButtonIAmDriver;

    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        final SharedPreferences.Editor editor =mPref.edit();
        mButtonIAmClient =findViewById(R.id.btn_IamClient);
        mButtonIAmDriver =findViewById(R.id.btn_IamDriver);

        mButtonIAmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "client");
                editor.apply();
                goToSelectAuth();
            }
        });
        mButtonIAmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "driver");
                editor.apply();
                goToSelectAuth();
            }
        });
    }

    //esto mantiene la sesion activa cuando el usuario se ha registrado
    @Override
    protected void onStart() {
        super.onStart();
        //esto quiere decir que si existe un usuario
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String user = mPref.getString("user", "");

            if (user.equals("client")){
                //Toast.makeText(loginActivity.this, "El login se realizo exitosamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapClientActivity.class);
                //Con el Flags evitamos que el conductor se pueda devolver al formulario de la registro
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                //Toast.makeText(loginActivity.this, "El email o password son incorrectos", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapDriverActivity.class);
                //Con el Flags evitamos que el conductor se pueda devolver al formulario de la registro
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void goToSelectAuth() {
        Intent intent = new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intent);
    }
}
