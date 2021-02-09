package com.example.uberclone.providers;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {

    private DatabaseReference mDatabaseReference;
    private GeoFire mGeoFire;


    public GeofireProvider(){
        //Nodo para conductores activos
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        mGeoFire =new GeoFire(mDatabaseReference);
    }

    //Guarda la localizacion del usuario con el id de user y la latitud y la longitud
    public void saveLocation(String idDriver, LatLng latLng){
        mGeoFire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    //eliminar la localizacion
    public void removeLocation(String idDriver){
        mGeoFire.removeLocation(idDriver);
    }


    //Metodo para obtener los conductores disponibles en un radio de 5Km con acceso a longitud y latitud
    public GeoQuery getActiveDrivers(LatLng latLng){
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 5);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}
