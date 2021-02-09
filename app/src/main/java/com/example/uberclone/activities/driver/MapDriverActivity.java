package com.example.uberclone.activities.driver;

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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uberclone.R;
import com.example.uberclone.activities.MainActivity;
import com.example.uberclone.activities.client.MapClientActivity;
import com.example.uberclone.include.MyToolbar;
import com.example.uberclone.providers.AuthProvider;
import com.example.uberclone.providers.GeofireProvider;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MapDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private GeofireProvider mGeofireProvider;

    //establecer icono como marcador
    private Marker mMarker;

    //boton de conectarse en el mapa
    private Button mButtonConnect;
    private boolean isConnect = false;

    //variable global para la localizacion y los metodos de la clase geofire
    private LatLng mCurrentLatLng;

    //variable para los permisos de ubicacion
    public static final int LOCATION_REQUEST_CODE=1;
    public static final int SETTINGS_REQUEST_CODE=2;

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult){
            for (Location location: locationResult.getLocations()){
                if (getApplicationContext() != null){
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //con la validacion se evita que se duplique el marcador
                    if (mMarker!= null){
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position
                            (new LatLng(location.getLatitude(),location.getLongitude())
                            ).title("Tu posición")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_personas_en_coche__vista_lateral_24))
                    );
                    // Obtiene la localizacion en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));

                    //metodo que actuliza la localizacion en la base de datos de firebase
                    updateLocation();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);
        MyToolbar.show(this, "Conductor", false);

        mAuthProvider = new AuthProvider();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mGeofireProvider = new GeofireProvider();

        mButtonConnect = findViewById(R.id.btnConnect);
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect){
                    disconnect();
                }else {
                    startLocation();
                }
            }
        });
    }

    //actuliza la localizacion verificando que la sesion exista y que la localizacion no se null
    private void updateLocation(){
        if (mAuthProvider.existSession() && mCurrentLatLng != null) {
            mGeofireProvider.saveLocation(mAuthProvider.getId(), mCurrentLatLng);
        }
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
                        //Punto de ubicacion en el mapa
                        mMap.setMyLocationEnabled(false);
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
            //Punto de ubicacion en el mapa
            mMap.setMyLocationEnabled(false);
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

    private void disconnect() {

        if (mFusedLocation != null){
            mButtonConnect.setText("CONECTARSE");
            isConnect= false;
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            //Valida que la sesion exista para removerla cuando se desconecte
            if (mAuthProvider.existSession()) {
                mGeofireProvider.removeLocation(mAuthProvider.getId());
            }
        }else {
            Toast.makeText(this, "No te puedes desconectar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void  startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mButtonConnect.setText("DESCONECTARSE");
                    isConnect= true;
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    //Punto de ubicacion en el mapa
                    mMap.setMyLocationEnabled(false);
                }else{
                    showAlertDialogGPS();
                }
            } else{
                checkLocationPermissions();
            }
        }else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                //Punto de ubicacion en el mapa
                mMap.setMyLocationEnabled(false);
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
                                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
        disconnect();
        mAuthProvider.logout();
        Intent intent = new Intent(MapDriverActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
