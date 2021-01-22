package com.example.uberclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth mAuth;

    public AuthProvider(){

        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout(){
        mAuth.signOut();
    }

    //Retorna el id del usuario
    public String getId(){
        return mAuth.getCurrentUser().getUid();
    }

    //Verifica que la sesion exista con el id del usuario
    public boolean existSession(){
        boolean exist = false;
        if (mAuth.getCurrentUser() != null){
            exist = true;
        }return exist;
    }
}
