package com.example.tablefind.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.example.tablefind.app_utilities.RestaurantAdapter;
import com.example.tablefind.data_models.Restaurant;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GridView lvList;
    private DrawerLayout drawerLayoutMain;
    private ActionBarDrawerToggle mToggle;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    RestaurantAdapter adapter;

    FusedLocationProviderClient fusedLocationProviderClient;

    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Restaurant List");

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        drawerLayoutMain = findViewById(R.id.drawerLayoutMain);
        mToggle = new ActionBarDrawerToggle(this, drawerLayoutMain, R.string.openDrawer, R.string.closeDrawer);

        drawerLayoutMain.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.mainNav);
        navigationView.setNavigationItemSelectedListener(this);

        lvList = findViewById(R.id.lvList);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            showProgress(true);
            tvLoad.setText("Getting All Restaurants...");
            getLocation();
            Backendless.Persistence.of(Restaurant.class).find(new AsyncCallback<List<Restaurant>>() {
                @Override
                public void handleResponse(List<Restaurant> response) {

                    ArrayList<Restaurant> restaurantsWLocation = new ArrayList<>();
                    Location location = new Location("");

                    for (Restaurant restaurant : response) {
                        String locationString = restaurant.getLocationGPS();
                        String[] separated = locationString.split(",");
                        location.setLatitude(Double.parseDouble(separated[0].trim()));
                        location.setLongitude(Double.parseDouble(separated[1].trim()));

                        Location myLocation = new Location("");
                        myLocation.setLongitude(addresses.get(0).getLongitude());
                        myLocation.setLatitude(addresses.get(0).getLatitude());

                        if (onLocationCLose(location, myLocation) == true)
                        {
                            restaurantsWLocation.add(restaurant);
                        }
                        else {

                        }
                    }
                    if (restaurantsWLocation == null || restaurantsWLocation.isEmpty()) {
                        ApplicationClass.showToast("No restaurants close to you!", 2, MainActivity.this);
                    } else {
                        ApplicationClass.restaurants = response;
                        adapter = new RestaurantAdapter(MainActivity.this, restaurantsWLocation);
                        lvList.setAdapter(adapter);
                    }

                    showProgress(false);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    ApplicationClass.showToast("Please make sure passwords match!", 2, MainActivity.this);
                    showProgress(false);
                }
            });
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Intent intent = new Intent(MainActivity.this, TableList.class);
                ApplicationClass.restaurant = ApplicationClass.restaurants.get(i);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            exitByBackKey();
        }
        if (id == R.id.receipt) {
            Intent intent = new Intent(MainActivity.this, ReceiptList.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TimePickerTheme)).setTitle("Return to Login").setMessage("Exit to Login Screen?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                emailEditor.putString("email", "");

                emailSharedPreferences = getSharedPreferences("email", 0);

                emailEditor = emailSharedPreferences.edit();

                emailEditor.putString("email", "").commit();
                MainActivity.this.finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setIcon(R.drawable.exit).show();

    }

    public Boolean onLocationCLose(Location myLocation, Location location) {

        double lat2;
        double lng2;
        double lat1;
        double lng1;

        lat1 = myLocation.getLatitude();
        lng1 = myLocation.getLongitude();
        lat2 = location.getLatitude();
        lng2 = location.getLongitude();
        if (distance(lat1, lng1, lat2, lng2) < 10)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation()
    {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null)
                {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        ApplicationClass.showToast(e.getMessage(), 2, MainActivity.this);
                    }
                }
                else
                {
                    ApplicationClass.showToast("No Location", 2, MainActivity.this);
                }
            }
        });
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }
}