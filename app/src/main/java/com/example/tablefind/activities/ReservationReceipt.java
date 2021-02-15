package com.example.tablefind.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import weborb.client.ant.wdm.Table;
/// <summary>
/// Creates a receipt with all relevant reservation information.
/// </summary>

public class ReservationReceipt extends AppCompatActivity {

    TextView receiptUser, receiptUserNumber, receiptUserEmail;
    TextView receiptRestaurantName, receiptRestaurantLocation, receiptRestaurantNumber;
    TextView receiptReservationFrom, receiptReservationTo, receiptReservationCapacity, receiptReservationTableInfo;
    Button receiptBtn, pdfBtn;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_receipt);
        setTitle("Details");

        /// Initialization vvv ///
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
        pdfBtn = findViewById(R.id.pdfBtn);

        showProgress(true);
        tvLoad.setText("Retrieving Details...");

        Backendless.Persistence.of(RestaurantTable.class).find(new AsyncCallback<List<RestaurantTable>>() {
            @Override
            public void handleResponse(List<RestaurantTable> response) {
                ApplicationClass.table = response.get(0);
                ApplicationClass.tables.addAll(response);

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

        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                exportAsPdf();
                ApplicationClass.showToast("Exported as My Details.pdf", 1, ReservationReceipt.this);
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
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TimePickerTheme)).setTitle("Any Issues?").setMessage("If you wish to cancel the reservation or make any changes in general, please contact the restaurant.").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportAsPdf()
    {
        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Paint myPaint = new Paint();
        int y = 10;
        int reservationNumberX = 1;
        int subHeadingX = 16;
        int dataX = 21;
        int counter = 0;

        for (Reservation reservation : ApplicationClass.reservations)
        {
            counter = counter + 1;
            myPage.getCanvas().drawText("Reservation " + counter, reservationNumberX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("User:" + "\n", subHeadingX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Name: " + ApplicationClass.user.getProperty("FirstName").toString().trim() + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Contact Number: " + ApplicationClass.user.getProperty("Cellphone").toString().trim() + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Email: " + ApplicationClass.user.getEmail().trim() + "\n", dataX, y, myPaint);
            y += 20;

            Restaurant thisRestaurant = new Restaurant();
            for (Restaurant restaurant: ApplicationClass.restaurants)
            {
                if (restaurant.getObjectId().equals(reservation.getRestaurantId()))
                {
                    thisRestaurant = restaurant;
                    break;
                }
            }
            myPage.getCanvas().drawText("Restaurant:" + "\n", subHeadingX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Name: " + thisRestaurant.getName().trim() + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Location: " + thisRestaurant.getLocationString().trim() + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Contact Number: " + thisRestaurant.getContactNumber().trim() + "\n", dataX, y, myPaint);
            y += 20;

            RestaurantTable thisTable = new RestaurantTable();
            for (RestaurantTable table : ApplicationClass.tables)
            {
                if (table.getObjectId().equals(reservation.getTableId()))
                {
                    thisTable = table;
                    break;
                }
            }
            myPage.getCanvas().drawText("Reservation:" + "\n", subHeadingX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("From: " + reservation.getTakenFrom().toString().trim() + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("To: " + reservation.getTakenTo().toString().trim() + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Capacity: " + thisTable.getCapacity() + "" + "\n", dataX, y, myPaint);
            y += 15;
            myPage.getCanvas().drawText("Table Info: " + thisTable.getTableInfo().trim() + "\n", dataX, y, myPaint);
            y += 40;
        }

        myPdfDocument.finishPage(myPage);
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/My Details.pdf";
        File myFile = new File(filePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
        } catch (Exception e) {
            ApplicationClass.showToast("Error: " + e.getMessage(), 2, ReservationReceipt.this);
        }
        myPdfDocument.close();
    }
}