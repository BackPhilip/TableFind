package com.example.tablefind.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewReservation extends AppCompatActivity {

    ImageView reservationImage;
    TextView reservationSeating, reservationInfo;
    EditText edtDuration;
    Button newReservationBtn;
    Calendar calendar = Calendar.getInstance();

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

        calendar.setTimeInMillis(getIntent().getLongExtra("Date",0));
        simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
        newReservationBtn.setText("Reserve Table for: " + simpleDateFormat.format(calendar.getTime()));

        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("seatings/" + ApplicationClass.table.getCapacity() + ".png");
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                reservationImage.setImageBitmap(ApplicationClass.getRoundedCornerBitmap(bm,100));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        reservationSeating.setText("Seating: " + ApplicationClass.table.getCapacity());
        reservationInfo.setText("Info: " + ApplicationClass.table.getTableInfo());

        newReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reserve();
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