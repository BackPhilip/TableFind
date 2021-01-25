package com.example.tablefind.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.example.tablefind.data_models.Reservation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewReservation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ImageView reservationImage;
    TextView reservationSeating, reservationInfo;
    EditText edtDuration;
    Button newReservationBtn;
    Calendar calendar = Calendar.getInstance();

    private DrawerLayout drawerLayoutMain;
    private ActionBarDrawerToggle mToggle;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    SimpleDateFormat simpleDateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);
        setTitle("New Reservation");

        reservationImage = findViewById(R.id.reservationImage);

        reservationSeating = findViewById(R.id.reservationSeating);
        reservationInfo = findViewById(R.id.reservationInfo);

        edtDuration = findViewById(R.id.edtDuration);

        newReservationBtn = findViewById(R.id.newReservationBtn);


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

        calendar.setTimeInMillis(getIntent().getLongExtra("Date",0));
        simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
        newReservationBtn.setText("Reserve Table for: " + simpleDateFormat.format(calendar.getTime()));

        switch (ApplicationClass.table.getCapacity())
        {
            case 1:
                reservationImage.setImageResource(R.mipmap.one_foreground);
                break;
            case 2:
                reservationImage.setImageResource(R.mipmap.two_foreground);
                break;
            case 3:
                reservationImage.setImageResource(R.mipmap.three_foreground);
                break;
            case 4:
                reservationImage.setImageResource(R.mipmap.four_foreground);
                break;
            case 5:
                reservationImage.setImageResource(R.mipmap.five_foreground);
                break;
            case 6:
                reservationImage.setImageResource(R.mipmap.six_foreground);
                break;
            case 7:
                reservationImage.setImageResource(R.mipmap.seven_foreground);
                break;
            case 8:
                reservationImage.setImageResource(R.mipmap.eight_foreground);
                break;
        }

        reservationSeating.setText("Seating: " + ApplicationClass.table.getCapacity());
        reservationInfo.setText("Info: " + ApplicationClass.table.getTableInfo());

        newReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reserve();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.logout)
        {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TimePickerTheme)).setTitle("Return to Login").setMessage("Exit to Login Screen?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(NewReservation.this, Login.class);
                    startActivity(intent);
                    SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                    SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                    emailEditor.putString("email","");

                    emailSharedPreferences = getSharedPreferences("email", 0);

                    emailEditor = emailSharedPreferences.edit();

                    emailEditor.putString("email", "").commit();
                    NewReservation.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setIcon(R.drawable.exit).show();
        }
        if (id == R.id.receipt)
        {
            Intent intent = new Intent(NewReservation.this, ReceiptList.class);
            startActivity(intent);
        }
        if (id == R.id.profile)
        {
            Intent intent = new Intent(NewReservation.this, Profile.class);
            startActivity(intent);
            NewReservation.this.finish();
        }
        return false;
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

    public void Reserve()
    {
        if (!(edtDuration.getText().toString().equals("1") || edtDuration.getText().toString().equals("2") || edtDuration.getText().toString().equals("3")))
        {
            ApplicationClass.showToast("Please Give a Duration from 1 - 3 Hours", 2, NewReservation.this);
        }
        else {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TimePickerTheme)) .setTitle("Confirm").setMessage("Are you sure you want to reserve a table for: " + simpleDateFormat.format(calendar.getTime())).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgress(true);
                    tvLoad.setText("Creating Reservation...");

                    final Reservation reservation = new Reservation();
                    reservation.setNumber(ApplicationClass.user.getProperty("Cellphone").toString());
                    reservation.setName(ApplicationClass.user.getProperty("FirstName").toString());
                    reservation.setTableId(ApplicationClass.table.getObjectId());
                    reservation.setTakenFrom(calendar.getTime());
                    calendar.add(Calendar.HOUR, Integer.parseInt(edtDuration.getText().toString()));
                    reservation.setTakenTo(calendar.getTime());
                    reservation.setUserId(ApplicationClass.user.getObjectId());
                    reservation.setRestaurantId(ApplicationClass.restaurant.getObjectId());

                    Backendless.Persistence.save(reservation, new AsyncCallback<Reservation>() {
                        @Override
                        public void handleResponse(Reservation response) {
                            ApplicationClass.showToast("Successfully Reserved!", 1, NewReservation.this);
                            showProgress(false);
                            ApplicationClass.reservation = reservation;
                            Intent intent = new Intent(NewReservation.this, ReservationReceipt.class);
                            startActivity(intent);
                            NewReservation.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            ApplicationClass.showToast("Failed: " + fault.getMessage(), 2, NewReservation.this);
                            showProgress(false);
                        }
                    });
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApplicationClass.showToast("Cancelled", 2, NewReservation.this);
                }
            }).setIcon(R.drawable.add).show();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        Intent intent = new Intent(NewReservation.this, TableList.class);
        startActivity(intent);
        NewReservation.this.finish();
    }
}