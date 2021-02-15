package com.example.tablefind.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;

public class Login extends AppCompatActivity {
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etMail, etPassword;
    Button btnLogin, btnRegister;
    TextView tvReset;
    Switch keepMeLoggedIn;

    String profileID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvReset = findViewById(R.id.tvReset);
        keepMeLoggedIn = findViewById(R.id.keepMeLoggedIn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMail.getText().toString().trim().isEmpty() || etPassword.getText().toString().trim().isEmpty())
                {
                    ApplicationClass.showToast("Please Enter All Fields", 2, Login.this);
                }
                else
                {
                    String email = etMail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    showProgress(true);
                    tvLoad.setText("Logging In...");

                    Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            if (response.getProperty("FirstName").toString().equals("deactivateRequest"))
                            {
                                ApplicationClass.showToast("Your Account is Deactivated", 2, Login.this);
                                new AlertDialog.Builder(new ContextThemeWrapper(Login.this, R.style.TimePickerTheme)).setTitle("Account Deactivated").setMessage("Would you like to request the reactivation of your account?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ApplicationClass.showToast("The Request has been sent, please try to login in later", 1, Login.this);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setIcon(R.drawable.exit).show();
                                showProgress(false);
                            }
                            else {
                                SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                                SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                                emailEditor.putString("email", "");

                                if (keepMeLoggedIn.isChecked()) {
                                    emailSharedPreferences = getSharedPreferences("email", 0);

                                    emailEditor = emailSharedPreferences.edit();

                                    emailEditor.putString("email", response.getUserId()).commit();
                                }
                                ApplicationClass.user = response;
                                ApplicationClass.showToast("Welcome " + response.getProperty("FirstName").toString(), 1, Login.this);
                                startActivity(new Intent(Login.this, MainActivity.class));
                                Login.this.finish();
                                showProgress(false);
                            }
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            if (fault.getMessage().equals("User cannot login - account is disabled"))
                            {
                                ApplicationClass.showToast("Your Account is Deactivated, Please Send an Email requesting reactivation", 2, Login.this);
                                showProgress(false);
                            }
                            else
                            {
                                ApplicationClass.showToast(fault.getMessage(), 2, Login.this);
                                showProgress(false);
                            }
                        }
                    }, true);
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMail.getText().toString().trim().isEmpty())
                {
                    ApplicationClass.showToast("Please Check Email Address!", 2, Login.this);
                }
                else
                {
                    String email = etMail.getText().toString().trim();

                    showProgress(true);
                    tvLoad.setText("Processing...");

                    Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                            ApplicationClass.showToast("Reset Instructions Sent to Email Address", 1, Login.this);
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            ApplicationClass.showToast("Error: " + fault.getMessage(), 2, Login.this);
                            showProgress(false);
                        }
                    });
                }
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
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences emailPreferences = getSharedPreferences("email", 0);
        profileID = emailPreferences.getString("email", "");
        if (!(profileID.equals("") || profileID.equals(null)))
        {
            showProgress(true);
            tvLoad.setText("Checking Login Credentials...");
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    if (response)
                    {
                        String userObjectId = UserIdStorageFactory.instance().getStorage().get();

                        Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                ApplicationClass.user = response;
                                startActivity(new Intent(Login.this, MainActivity.class));
                                ApplicationClass.showToast("Welcome " + response.getProperty("FirstName").toString(), 1, Login.this);
                                Login.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                ApplicationClass.showToast("Error: " + fault.getMessage(), 2, Login.this);
                                showProgress(false);
                            }
                        });
                    }
                    else
                    {
                        showProgress(false);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        }
    }
}