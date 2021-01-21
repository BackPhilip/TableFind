package com.example.tablefind.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.example.tablefind.data_models.Reservation;
import com.example.tablefind.data_models.Restaurant;
import com.example.tablefind.data_models.RestaurantTable;

import java.util.List;

public class ReservationReceipt extends AppCompatActivity {

    TextView receiptUser, receiptUserNumber, receiptUserEmail;
    TextView receiptRestaurantName, receiptRestaurantLocation, receiptRestaurantNumber;
    TextView receiptReservationFrom, receiptReservationTo, receiptReservationCapacity, receiptReservationTableInfo;
    Button receiptBtn;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_receipt);
        setTitle("Receipt");

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        receiptUser = findViewById(R.id.receiptUser);
        receiptUserNumber = findViewById(R.id.receiptUserNumber);
        receiptUserEmail = findViewById(R.id.receiptUserEmail);

        receiptRestaurantName = findViewById(R.id.receiptRestaurantName);
        receiptRestaurantLocation = findViewById(R.id.receiptRestaurantLocation);
        receiptRestaurantNumber = findViewById(R.id.receiptRestaurantNumber);

        receiptReservationFrom = findViewById(R.id.receiptReservationFrom);
        receiptReservationTo = findViewById(R.id.receiptReservationTo);
        receiptReservationCapacity = findViewById(R.id.receiptReservationCapacity);
        receiptReservationTableInfo = findViewById(R.id.receiptReservationTableInfo);

        receiptBtn = findViewById(R.id.receiptBtn);

        if (ApplicationClass.reservation != null)
        {
            receiptUser.setText(ApplicationClass.user.getProperty("FirstName").toString().trim() + " " + ApplicationClass.user.getProperty("LastName").toString().trim());
            receiptUserNumber.setText(ApplicationClass.user.getProperty("Cellphone").toString().trim());
            receiptUserEmail.setText(ApplicationClass.user.getEmail());

            receiptRestaurantName.setText(ApplicationClass.restaurant.getName());
            receiptRestaurantLocation.setText(ApplicationClass.restaurant.getLocationString() + "(Click Here For Google Maps Location)");
            receiptRestaurantNumber.setText(ApplicationClass.restaurant.getContactNumber());

            receiptReservationFrom.setText(ApplicationClass.reservation.getTakenFrom().toString());
            receiptReservationTo.setText(ApplicationClass.reservation.getTakenTo().toString());
            receiptReservationCapacity.setText(ApplicationClass.table.getCapacity() + "");
            receiptReservationTableInfo.setText(ApplicationClass.table.getTableInfo());
        }

        else
        {
            showProgress(true);
            tvLoad.setText("Retrieving Receipt...");
            String whereClause = "userId = '" + ApplicationClass.user.getObjectId().trim() + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);

            Backendless.Persistence.of(Reservation.class).find(queryBuilder, new AsyncCallback<List<Reservation>>() {
                @Override
                public void handleResponse(List<Reservation> response) {
                    if (response.size() == 0)
                    {
                        showProgress(false);
                        mLoginFormView.setVisibility(View.GONE);
                        receiptBtn.setVisibility(View.VISIBLE);
                        receiptBtn.setText("You Have No Reservations(Return)");
                        receiptBtn.setTextColor(Color.RED);
                    }
                    else {
                        ApplicationClass.reservation = response.get(0);

                        String whereClause = "objectId = '" + ApplicationClass.reservation.getTableId() + "'";
                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                        queryBuilder.setWhereClause(whereClause);

                        Backendless.Persistence.of(RestaurantTable.class).find(queryBuilder, new AsyncCallback<List<RestaurantTable>>() {
                            @Override
                            public void handleResponse(List<RestaurantTable> response) {
                                ApplicationClass.table = response.get(0);

                                String whereClause = "objectId = '" + ApplicationClass.reservation.getRestaurantId() + "'";
                                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                queryBuilder.setWhereClause(whereClause);

                                Backendless.Persistence.of(Restaurant.class).find(queryBuilder, new AsyncCallback<List<Restaurant>>() {
                                    @Override
                                    public void handleResponse(List<Restaurant> response) {
                                        ApplicationClass.restaurant = response.get(0);

                                        receiptUser.setText(ApplicationClass.user.getProperty("FirstName").toString().trim() + " " + ApplicationClass.user.getProperty("LastName").toString().trim());
                                        receiptUserNumber.setText(ApplicationClass.user.getProperty("Cellphone").toString().trim());
                                        receiptUserEmail.setText(ApplicationClass.user.getEmail());

                                        receiptRestaurantName.setText(ApplicationClass.restaurant.getName());
                                        receiptRestaurantLocation.setText(ApplicationClass.restaurant.getLocationString() + "(Click Here For Google Maps Location)");
                                        receiptRestaurantNumber.setText(ApplicationClass.restaurant.getContactNumber());

                                        receiptReservationFrom.setText(ApplicationClass.reservation.getTakenFrom().toString());
                                        receiptReservationTo.setText(ApplicationClass.reservation.getTakenTo().toString());
                                        receiptReservationCapacity.setText(ApplicationClass.table.getCapacity() + "");
                                        receiptReservationTableInfo.setText(ApplicationClass.table.getTableInfo());

                                        showProgress(false);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        ApplicationClass.showToast("Error: " + fault.getMessage(), 2, ReservationReceipt.this);
                                        showProgress(false);
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                ApplicationClass.showToast("Error: " + fault.getMessage(), 2, ReservationReceipt.this);
                                showProgress(false);
                            }
                        });
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    ApplicationClass.showToast("Error: " + fault.getMessage(), 2, ReservationReceipt.this);
                    showProgress(false);
                }
            });
        }

        receiptRestaurantLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse(ApplicationClass.restaurant.getLocationGPS());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        receiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitByBackKey();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TimePickerTheme)).setTitle("Any Issues?").setMessage("If you wish to cancel a reservation or make any changes in general, please contact the restaurant.").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ReservationReceipt.this, MainActivity.class);
                startActivity(intent);
                ReservationReceipt.this.finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setIcon(R.drawable.exit).show();
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
}