package com.example.uberclone.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uberclone.R;
import com.example.uberclone.include.MyToolbar;
import com.example.uberclone.models.Client;
import com.example.uberclone.providers.AuthProvider;
import com.example.uberclone.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import dmax.dialog.SpotsDialog;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    ClientProvider mClientProvider;
    AlertDialog mDialog;

    //VIEWS
    Button mButtonRegister;
    TextInputEditText mTextInputName, mTextInputEmail, mTextInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();

       MyToolbar.show(this, "Resgistro de Usuario", true);

       mAuthProvider = new AuthProvider();
       mClientProvider = new ClientProvider();

        //String selectedUser = mPref.getString("user", "");
        //Toast.makeText(this, "El valor seleccionado es " + selectedUser, Toast.LENGTH_SHORT).show();
        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });
    }

    private void clickRegister() {
        final String name = mTextInputName.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if (password.length() >=6){
                mDialog.show();
                register(name, email, password);
            }else {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(final String name, final String email, String password) {
             mAuthProvider.register(email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     mDialog.hide();
                     if (task.isSuccessful()){
                         String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                         Client client = new Client(name, email, id);
                         create(client);
                     }else {
                         Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_SHORT).show();
                     }
                 }
             });
    }

    public void create(Client cliente){
        mClientProvider.create(cliente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "El registro se realizo", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * private void saveUser(String id, String name, String email) {
     *         String selectedUser = mPref.getString("user", "");
     *         User user = new User();
     *         user.setEmail(email);
     *         user.setName(name);
     *
     *         if (selectedUser.equals("driver")){
     *             mDatabase.child("Users").child("Drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
     *                 @Override
     *                 public void onComplete(@NonNull Task<Void> task) {
     *                     if (task.isSuccessful()){
     *                         Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
     *                     }else {
     *                         Toast.makeText(RegisterActivity.this, "Fallo el registro ", Toast.LENGTH_SHORT).show();
     *                     }
     *                 }
     *             });
     *
     *         }else if (selectedUser.equals("client")){
     *             mDatabase.child("Users").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
     *                 @Override
     *                 public void onComplete(@NonNull Task<Void> task) {
     *                     if (task.isSuccessful()){
     *                         Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
     *                     }else{
     *                         Toast.makeText(RegisterActivity.this, "Fallo el registro", Toast.LENGTH_SHORT).show();
     *                     }
     *                 }
     *             });
     *         }
     *     }
     */
}
