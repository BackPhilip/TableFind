package com.example.tablefind.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
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

import java.util.List;

public class Menu extends AppCompatActivity {

    ListView menuLvList;

    TextView pdfLink;

    MenuAdapter adapter;

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

        showProgress(true);
        tvLoad.setText("Getting Dishes...");

        String whereClause = "restaurantId = '" + ApplicationClass.restaurant.getObjectId().trim() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setGroupBy("name");

        Backendless.Persistence.of(RestaurantMenuItem.class).find(queryBuilder, new AsyncCallback<List<RestaurantMenuItem>>() {
            @Override
            public void handleResponse(List<RestaurantMenuItem> response) {
                ApplicationClass.menuItems = response;
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
        pdfLink.setTextColor(Color.BLUE);
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