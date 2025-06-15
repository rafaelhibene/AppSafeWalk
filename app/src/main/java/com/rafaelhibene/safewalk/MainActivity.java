package com.rafaelhibene.safewalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.rafaelhibene.safewalk.helpers.DirectionHelper;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bnv_bottom);
        bottomNavigationView.setSelectedItemId(R.id.tab_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tab_home) {
                return true;
            } else if (id == R.id.tab_emergencia) {
                startActivity(new Intent(MainActivity.this, EmergenciaActivity.class));
                return true;
            } else if (id == R.id.tab_perfil) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                return true;
            }
            return false;
        });

        // Inicializa Places API
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = ai.metaData.getString("com.google.android.libraries.places.API_KEY");
            if (apiKey != null && !Places.isInitialized()) {
                Places.initialize(getApplicationContext(), apiKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Erro ao inicializar Places API", e);
        }

        // Configura o Autocomplete
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS));
            autocompleteFragment.setHint("Digite o endereço de destino");

            // Ajusta tamanho da fonte e adiciona borda cinza ao redor do campo de busca
            autocompleteFragment.getView().post(() -> {
                try {
                    EditText etPlace = autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);
                    etPlace.setTextSize(18); // Ajuste o tamanho aqui (em sp)
                    etPlace.setBackgroundResource(R.drawable.autocomplete_background); // Aplica borda
                    // Opcional: ajustar padding horizontal para ficar melhor visualmente
                    int paddingVertical = etPlace.getPaddingTop();
                    etPlace.setPadding(40, paddingVertical, 40, paddingVertical);
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao ajustar tamanho da fonte e borda do campo de busca", e);
                }
            });


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    if (place.getLatLng() != null && mMap != null) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions()
                                .position(place.getLatLng())
                                .title(place.getName()));


                        // Verifica a permissão antes de acessar a localização
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(location -> {
                                        if (location != null) {
                                            LatLng origem = new LatLng(location.getLatitude(), location.getLongitude());
                                            LatLng destino = place.getLatLng();

                                            //move a camera ao ponto de partida
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origem, 15));
                                            try {
                                                ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                                String apiKey = ai.metaData.getString("com.google.android.geo.API_KEY");
                                                DirectionHelper.drawRoute(mMap, origem, destino, apiKey);
                                            } catch (PackageManager.NameNotFoundException e) {
                                                Log.e(TAG, "Erro ao obter API Key", e);
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Localização atual não encontrada", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Log.e(TAG, "Erro ao selecionar local: " + status.getStatusMessage());
                    Toast.makeText(MainActivity.this, "Erro ao selecionar local", Toast.LENGTH_SHORT).show();
                }
            });
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionGranted) {
            enableMyLocation();
        } else {
            LatLng fallback = new LatLng(-15.793889, -47.882778);
            mMap.addMarker(new MarkerOptions().position(fallback).title("Localização padrão"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 15));
        }
    }

    private void enableMyLocation() {
        if (mMap == null) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(userLatLng).title("Você está aqui"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                    } else {
                        LatLng fallback = new LatLng(-15.793889, -47.882778);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(fallback).title("Localização padrão"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 15));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            locationPermissionGranted = grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (locationPermissionGranted) {
                enableMyLocation();
            } else if (mMap != null) {
                LatLng fallback = new LatLng(-15.793889, -47.882778);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(fallback).title("Localização padrão"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 15));
            }
        }
    }
}
