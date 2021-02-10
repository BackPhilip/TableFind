package com.example.tablefind.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import com.example.tablefind.data_models.RestaurantMenuItem;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class Menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ListView menuLvList;

    TextView pdfLink;

    MenuAdapter adapter;

    private DrawerLayout drawerLayoutMain;
    private ActionBarDrawerToggle mToggle;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle("Menu");

        menuLvList = findViewById(R.id.menuLvList);

        pdfLink = findViewById(R.id.pdfLink);

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

        showProgress(true);
        tvLoad.setText("Getting Dishes...");

        String whereClause = "restaurantId = '" + ApplicationClass.restaurant.getObjectId().trim() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setGroupBy("name");

        Backendless.Persistence.of(RestaurantMenuItem.class).find(queryBuilder, new AsyncCallback<List<RestaurantMenuItem>>() {
            @Override
            public void handleResponse(List<RestaurantMenuItem> response) {
                ApplicationClass.menuItems.addAll(response);
                adapter = new MenuAdapter(Menu.this, response);
                menuLvList.setAdapter(adapter);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                ApplicationClass.showToast("Error: " + fault.getMessage(), 2, Menu.this);
                showProgress(false);
            }
        });

        menuLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Menu.this, MenuItemDetail.class);
                ApplicationClass.menuItem = ApplicationClass.menuItems.get(i);
                startActivity(intent);
                Menu.this.finish();
            }
        });

        pdfLink.setClickable(true);
        pdfLink.setMovementMethod(LinkMovementMethod.getInstance());
        pdfLink.setText(Html.fromHtml("<a href='" + ApplicationClass.restaurant.getMenuLink() + "'> Tap Here for PDF Menu </a>", Html.FROM_HTML_MODE_COMPACT));
        pdfLink.setTextColor(Color.RED);
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
                    Intent intent = new Intent(Menu.this, Login.class);
                    startActivity(intent);
                    SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                    SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                    emailEditor.putString("email","");

                    emailSharedPreferences = getSharedPreferences("email", 0);

                    emailEditor = emailSharedPreferences.edit();

                    emailEditor.putString("email", "").commit();
                    Menu.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setIcon(R.drawable.exit).show();
        }
        if (id == R.id.receipt)
        {
            Intent intent = new Intent(Menu.this, ReceiptList.class);
            startActivity(intent);
            Menu.this.finish();
        }
        if (id == R.id.profile)
        {
            Intent intent = new Intent(Menu.this, Profile.class);
            startActivity(intent);
            Menu.this.finish();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        Intent intent = new Intent(Menu.this, TableList.class);
        startActivity(intent);
        Menu.this.finish();
    }
}