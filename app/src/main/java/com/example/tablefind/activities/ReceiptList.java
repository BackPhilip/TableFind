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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.example.tablefind.app_utilities.MenuAdapter;
import com.example.tablefind.app_utilities.ReceiptAdapter;
import com.example.tablefind.data_models.Reservation;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
/// <summary>
/// Generates a document with a list of all reservations
/// </summary>

public class ReceiptList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ListView receiptLvList;

    ReceiptAdapter adapter;

    private DrawerLayout drawerLayoutMain;
    private ActionBarDrawerToggle mToggle;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);
        setTitle("Reservation List");

        /// Initialization vvv ///
        receiptLvList = findViewById(R.id.receiptLvList);

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

        String whereClause = "userId = '" + ApplicationClass.user.getObjectId() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        showProgress(true);
        tvLoad.setText("Retrieving Reservation List...");

        Backendless.Persistence.of(Reservation.class).find(queryBuilder, new AsyncCallback<List<Reservation>>() {
            @Override
            public void handleResponse(List<Reservation> response) {
                if (response.size() != 0)
                {
                    ApplicationClass.reservations.addAll(response);
                    adapter = new ReceiptAdapter(ReceiptList.this, response);
                    receiptLvList.setAdapter(adapter);
                    showProgress(false);
                }
                else
                {
                    ApplicationClass.showToast("You Have No Reservations!", 2, ReceiptList.this);
                    Intent intent = new Intent(ReceiptList.this, MainActivity.class);
                    startActivity(intent);
                    ReceiptList.this.finish();
                    showProgress(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
                ApplicationClass.showToast(fault.getMessage(), 2, ReceiptList.this);
            }
        });

       receiptLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ReceiptList.this, ReservationReceipt.class);
                ApplicationClass.reservation = ApplicationClass.reservations.get(i);
                startActivity(intent);
                ReceiptList.this.finish();
            }
        });
    }

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
                    Intent intent = new Intent(ReceiptList.this, Login.class);
                    startActivity(intent);
                    SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                    SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                    emailEditor.putString("email","");

                    emailSharedPreferences = getSharedPreferences("email", 0);

                    emailEditor = emailSharedPreferences.edit();

                    emailEditor.putString("email", "").commit();
                    ReceiptList.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setIcon(R.drawable.exit).show();
        }
        if (id == R.id.receipt)
        {
            Intent intent = new Intent(ReceiptList.this, ReceiptList.class);
            startActivity(intent);
            ReceiptList.this.finish();
        }
        if (id == R.id.profile)
        {
            Intent intent = new Intent(ReceiptList.this, Profile.class);
            startActivity(intent);
            ReceiptList.this.finish();
        }
        return false;
    }//end method

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
    //Purpose          : ***
    //Re-use           : none
    //Input Parameters : none
    //Output Type      : void
    //

    protected void exitByBackKey() {
        Intent intent = new Intent(ReceiptList.this, MainActivity.class);
        startActivity(intent);
        ReceiptList.this.finish();
    }//end method
}