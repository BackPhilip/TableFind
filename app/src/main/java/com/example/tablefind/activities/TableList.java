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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.example.tablefind.app_utilities.TableAdapter;
import com.example.tablefind.data_models.Reservation;
import com.example.tablefind.data_models.RestaurantTable;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/// <summary>
/// Retrieves and displays all restaurant tables.
/// </summary>

public class TableList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    GridView tableLvList;
    TextView showLocation, restaurantNameText;
    Button edtDateTime, menuBtn;

    private DrawerLayout drawerLayoutMain;
    private ActionBarDrawerToggle mToggle;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    final Calendar calendar = Calendar.getInstance();

    TableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);
        setTitle("Table List");

        /// Initialization vvv ///
        tableLvList = findViewById(R.id.tableLvList);
        showLocation = findViewById(R.id.showLocation);
        edtDateTime = findViewById(R.id.edtDateTime);
        menuBtn = findViewById(R.id.menuBtn);
        restaurantNameText = findViewById(R.id.restaurantNameText);

        edtDateTime.setInputType(InputType.TYPE_NULL);

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

        restaurantNameText.setText(ApplicationClass.restaurant.getName());
        showLocation.setTextColor(Color.GREEN);

        String whereClause = "restaurantId = '" + ApplicationClass.restaurant.getObjectId().trim() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);

        Backendless.Persistence.of(RestaurantTable.class).find(queryBuilder, new AsyncCallback<List<RestaurantTable>>() {
            @Override
            public void handleResponse(List<RestaurantTable> response) {
                for (RestaurantTable table : response)
                {
                    if (table.isAvailable())
                    {
                        ApplicationClass.tables.add(table);
                    }
                }
                for (RestaurantTable table : ApplicationClass.tables)
                {
                    table.setAvailable(true);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                ApplicationClass.showToast("Error: " + fault.getMessage(), 2, TableList.this);
                showProgress(false);
            }
        });

        edtDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(edtDateTime);
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TableList.this, com.example.tablefind.activities.Menu.class);
                startActivity(intent);
                TableList.this.finish();
                ApplicationClass.tables.clear();
            }
        });

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse("geo:" + ApplicationClass.restaurant.getLocationGPS()));
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        tableLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (ApplicationClass.tables.get(i).isAvailable())
                {
                    Intent intent = new Intent(TableList.this, NewReservation.class);
                    ApplicationClass.table = ApplicationClass.tables.get(i);
                    intent.putExtra("Date", calendar.getTimeInMillis());
                    startActivity(intent);
                    TableList.this.finish();
                }
                else
                {
                    ApplicationClass.showToast("Table is not available at that time!", 2, TableList.this);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)

    //
    //Method Name      : void showProgress()
    //Purpose          : Initialise and instantiate the progress bar and progress text
    //Re-use           : in OnCreate()
    //Input Parameters : boolean
    //Output Type      : void
    //

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
    }//end method

    //
    //Method Name      : void showDateTimeDialog()
    //Purpose          : Displays dialogue box for user to select date and time.
    //Re-use           : none
    //Input Parameters : Button
    //Output Type      : void
    //

    private void showDateTimeDialog(final Button edtDateTime) {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                datePicker.setBackgroundColor(Color.RED);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
                        Calendar openCal = Calendar.getInstance();
                        openCal.setTime(ApplicationClass.restaurant.getOpen());
                        Calendar closeCal = Calendar.getInstance();
                        closeCal.setTime(ApplicationClass.restaurant.getClose());

                        if (calendar.getTime().before(Calendar.getInstance().getTime()))
                        {
                            edtDateTime.setText("Choose a Valid Time!");
                            edtDateTime.setTextColor(Color.RED);
                            ApplicationClass.showToast("Please Choose a time Forward from the Current Time", 2, TableList.this);
                            tableLvList.setVisibility(View.GONE);
                        }
                        else
                        {
                            if (openCal.get(Calendar.HOUR_OF_DAY) < (calendar.get(Calendar.HOUR_OF_DAY)) && closeCal.get(Calendar.HOUR_OF_DAY) > (calendar.get(Calendar.HOUR_OF_DAY)))
                            {
                                edtDateTime.setText(simpleDateFormat.format(calendar.getTime()));
                                edtDateTime.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                TimeFilter(calendar.getTime());
                                tableLvList.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                edtDateTime.setText("Choose a Valid Time!");
                                edtDateTime.setTextColor(Color.RED);
                                ApplicationClass.showToast("The restaurant is closed at that time!", 2, TableList.this);
                                tableLvList.setVisibility(View.GONE);
                            }
                        }
                    }
                };

                new TimePickerDialog(TableList.this, R.style.TimePickerTheme, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(TableList.this, R.style.TimePickerTheme, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }//end method

    //
    //Method Name      : void TimeFilter()
    //Purpose          : Shows available tables for selected time frame.
    //Re-use           : showProgress()
    //Input Parameters : Date
    //Output Type      : void
    //

    private void TimeFilter(final Date requiredDate)
    {
            showProgress(true);
            tvLoad.setText("Filtering...");

            String reservationWhereClause = "restaurantId = '" + ApplicationClass.restaurant.getObjectId().trim() + "'";
            DataQueryBuilder reservationQueryBuilder = DataQueryBuilder.create();
            reservationQueryBuilder.setWhereClause(reservationWhereClause);
            reservationQueryBuilder.setGroupBy("tableId");
            reservationQueryBuilder.setPageSize(100);

            Backendless.Persistence.of(Reservation.class).find(reservationQueryBuilder, new AsyncCallback<List<Reservation>>() {
                @Override
                public void handleResponse(List<Reservation> response) {

                    ApplicationClass.reservations.addAll(response);

                    for (RestaurantTable table : ApplicationClass.tables)
                    {
                        table.setAvailable(true);
                    }

                    for (Reservation reservation : response) {
                        if (requiredDate.after(reservation.getTakenFrom()) && requiredDate.before(reservation.getTakenTo())) {
                            for (RestaurantTable table : ApplicationClass.tables) {
                                if (table.getObjectId().equals(reservation.getTableId())) {
                                    table.setAvailable(false);
                                }
                            }
                        }
                    }
                    adapter = new TableAdapter(TableList.this, ApplicationClass.tables);
                    tableLvList.setAdapter(adapter);

                    if (!(ApplicationClass.tables.size() == 0))
                    {
                        tableLvList.setVisibility(View.VISIBLE);
                    }
                    else
                        {
                        tableLvList.setVisibility(View.GONE);
                    }
                    showProgress(false);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    ApplicationClass.showToast("Error: " + fault.getMessage(), 2, TableList.this);
                    showProgress(false);
                }
            });
    }//end method

    //
    //Method Name      : boolean onOptionItemSelected()
    //Purpose          : Passes the selected item.
    //Re-use           :
    //Input Parameters : MenuItem
    //Output Type      : boolean
    //

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//end method

    //
    //Method Name      : boolean onNavigationItemSelected()
    //Purpose          : implements the selected navigation function.
    //Re-use           : none
    //Input Parameters : MenuItem
    //Output Type      : boolean
    //

    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.logout)
        {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TimePickerTheme)).setTitle("Return to Login").setMessage("Exit to Login Screen?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(TableList.this, Login.class);
                    startActivity(intent);
                    SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                    SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                    emailEditor.putString("email","");

                    emailSharedPreferences = getSharedPreferences("email", 0);

                    emailEditor = emailSharedPreferences.edit();

                    emailEditor.putString("email", "").commit();
                    TableList.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setIcon(R.drawable.exit).show();
        }
        if (id == R.id.receipt)
        {
            Intent intent = new Intent(TableList.this, ReceiptList.class);
            startActivity(intent);
            TableList.this.finish();
        }
        if (id == R.id.profile)
        {
            Intent intent = new Intent(TableList.this, Profile.class);
            startActivity(intent);
            TableList.this.finish();
        }
        return false;
    }//end method

    //
    //Method Name      : boolean onKeyDown()
    //Purpose          : functionality for the back key.
    //Re-use           : exitByBackKey()
    //Input Parameters : int, KeyEvent
    //Output Type      : boolean
    //

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }//end method

    //
    //Method Name      : void exitByBackKey()
    //Purpose          : logic that is executed when the back key is hit
    //Re-use           : none
    //Input Parameters : none
    //Output Type      : void
    //

    protected void exitByBackKey() {
        Intent intent = new Intent(TableList.this, MainActivity.class);
        startActivity(intent);
        TableList.this.finish();
        ApplicationClass.tables.clear();
    }//end method
}