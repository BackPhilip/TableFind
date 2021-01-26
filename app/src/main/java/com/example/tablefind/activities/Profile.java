package com.example.tablefind.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;

public class Profile extends AppCompatActivity {

TextView profileName, profileNumber, profileEmail;
Button profileReturnBtn, profileDeactivateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("My Profile");

        profileName = findViewById(R.id.profileName);
        profileNumber = findViewById(R.id.profileNumber);
        profileEmail = findViewById(R.id.profileEmail);

        profileReturnBtn = findViewById(R.id.profileReturnBtn);
        profileDeactivateBtn = findViewById(R.id.profileDeactivateBtn);

        profileName.setText(ApplicationClass.user.getProperty("FirstName").toString() + " " + ApplicationClass.user.getProperty("LastName").toString());
        profileNumber.setText(ApplicationClass.user.getProperty("Cellphone").toString());
        profileEmail.setText(ApplicationClass.user.getEmail());

        profileReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                Profile.this.finish();
            }
        });

        profileDeactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(new ContextThemeWrapper(Profile.this, R.style.TimePickerTheme)).setTitle("Confirm Deactivation").setMessage("Are you sure you want to deactivate this profile?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ApplicationClass.user.setProperty("FirstName", "deactivateRequest");
                        Backendless.UserService.update(ApplicationClass.user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                SharedPreferences emailSharedPreferences = getSharedPreferences("email", 0);
                                SharedPreferences.Editor emailEditor = emailSharedPreferences.edit();
                                emailEditor.putString("email","");

                                emailSharedPreferences = getSharedPreferences("email", 0);

                                emailEditor = emailSharedPreferences.edit();

                                emailEditor.putString("email", "").commit();
                                ApplicationClass.showToast("Deactivation Request has been Sent!" ,1, Profile.this);
                                startActivity(new Intent(Profile.this, Login.class));
                                Profile.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                ApplicationClass.showToast("Error: " + fault.getMessage(),1, Profile.this);
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setIcon(R.drawable.info).show();
            }
        });
    }
}