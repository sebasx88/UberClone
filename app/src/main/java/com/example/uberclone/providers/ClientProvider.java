package com.example.uberclone.providers;

import android.net.http.SslCertificate;

import com.example.uberclone.models.Client;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class ClientProvider {

    DatabaseReference mDatabase;

    public ClientProvider (){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create (Client client){

        Map<String, Object> map = new HashMap<>();
        map.put("name", client.getName());
        map.put("email", client.getEmail());

        return mDatabase.child(client.getId()).setValue(map);

        /**
         * con esta linea se pasa el objeto cliente donde se enviara el email, name  y  id si no
         * necesitamos algun atributo hacemos lo de las lineas superiores.
         * return mDatabase.child(client.getId()).setValue(client);
         */

    }
}
