package com.example.uberclone.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.example.uberclone.R;
import com.example.uberclone.activities.MainActivity;
import com.example.uberclone.activities.driver.MapDriverActivity;
import com.example.uberclone.include.MyToolbar;
import com.example.uberclone.providers.AuthProvider;
import com.example.uberclone.providers.GeofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    // propiedad para el marcador de la ubicacion
    private Marker mMarker;

    //georeferencicion de la ubicacion
    private GeofireProvider mGeofireProvider;

    // lista de los marcadores de los conductores
    private List<Marker> mDriversMarkers = new ArrayList<>();

    //variable para guardar lat y lon
    private LatLng mCurrentLatLng;

    //variable para los permisos de ubicacion
    public static final int LOCATION_REQUEST_CODE=1;
    public static final int SETTINGS_REQUEST_CODE=2;

    //variable boolena que permite que solo se ejecute una sola vez metodo getActiveDrivers ya que pore estar en el callback se ejecutaria varias veces
    private boolean mFirstTime = true ;

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult){
            for (Location location: locationResult.getLocations()){
                if (getApplicationContext() != null){
                    //con la validacion se evita que se duplique el marcador
                    if (mMarker!= null){
                        mMarker.remove();
                    }
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMarker = mMap.addMarker(new MarkerOptions().position
                            (new LatLng(location.getLatitude(),location.getLongitude())
                            ).title("Tu posición")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicaci_n_user))
                    );
                    // Obtiene la localizacion en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));

                    // la variable se encuentra en verdadero se valida la condicion cambia a false y se ejecuta una sola vez el metodo getActiveDrivres
                    if (mFirstTime){
                        mFirstTime = false;
                        getActiveDrivers();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);
        MyToolbar.show(this, "Cliente", false);

        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeofireProvider();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    //busca los conductores disponibles
    private void getActiveDrivers(){
        mGeofireProvider.getActiveDrivers(mCurrentLatLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            // se añadiran los marcadores de los conductores que se conectan
            public void onKeyEntered(String key, GeoLocation location) {
                for (Marker marker: mDriversMarkers){
                    //verifica si en la lista hay marcadores con identificador
                    if (marker.getTag() != null){
                        // el key es el identificador de la base de datos si se conecta un conductor
                        if (marker.getTag().equals(key)){
                            // con el return no se add de nuevo el marcador del conductor para el cliente
                            return;
                        }
                    }
                }
                //se guarda la posicion del lat y long del conductor que se conecto
                LatLng driverLatLng = new LatLng(location.latitude, location.latitude);
                //le creamos un icono a la posicion
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_personas_en_coche__vista_lateral_24)));
                // el tag sera el id del conductor que se toma del key que se recibe por parametro
                marker.setTag(key);
                //añadiremos el marcador a la lista de marcadores
                mDriversMarkers.add(marker);
            }


            //Cuando un conductor se halla desconectado
            @Override
            public void onKeyExited(String key) {
                for (Marker marker: mDriversMarkers){
                    //verifica si en la lista hay marcadores con identificador
                    if (marker.getTag() != null){
                        // el key es el identificador de la base de datos si se conecta un conductor
                        if (marker.getTag().equals(key)){
                            //removemos el marcador
                            marker.remove();
                            //eliminamos de la lista de marcadores
                            mDriversMarkers.remove(marker);
                            // con el return no se add de nuevo el marcador del conductor para el cliente
                            return;
                        }
                    }
                }

            }


            @Override
            // se actualiza la posicion del conductor en tiempo real
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker marker: mDriversMarkers){
                    //verifica si en la lista hay marcadores con identificador
                    if (marker.getTag() != null){
                        // el key es el identificador de la base de datos si se conecta un conductor
                        if (marker.getTag().equals(key)){
                           // le decimos a cada marcador que se actualice la posicion
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY );
        mLocationRequest.setSmallestDisplacement(5);
        startLocation();
    }

    //activando los permisos de usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }else{
                        showAlertDialogGPS();
                    }

                }else{
                    checkLocationPermissions();
                }
            }else{
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()){
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }else {
            showAlertDialogGPS();
        }
    }

    private void showAlertDialogGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }return isActive;
    }

    private void  startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }else{
                    showAlertDialogGPS();
                }
            } else{
                checkLocationPermissions();
            }
        }else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }else {
                showAlertDialogGPS();
            }
        }
    }
    private void checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                ActivityCompat.requestPermissions(MapClientActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(MapClientActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    void logout(){
        mAuthProvider.logout();
        Intent intent = new Intent(MapClientActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
