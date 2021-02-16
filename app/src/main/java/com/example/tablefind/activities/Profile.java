package com.example.tablefind.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
/// <summary>
/// Shows user profile and allows for deactivation.
/// </summary>

public class Profile extends AppCompatActivity {

TextView profileName, profileNumber, profileEmail;
Button profileReturnBtn, profileDeactivateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("My Profile");

        /// Initialization vvv ///
        profileName = findViewById(R.id.profileName);
        profileNumber = findViewById(R.id.profileNumber);
        profileEmail = findViewById(R.id.profileEmail);

        profileReturnBtn = findViewById(R.id.profileReturnBtn);
        profileDeactivateBtn = findViewById(R.id.profileDeactivateBtn);

        profileName.setText(ApplicationClass.user.getProperty("FirstName").toString() + " " + ApplicationClass.user.getProperty("LastName").toString());
        profileNumber.setText(ApplicationClass.user.getProperty("Cellphone").toString());
        profileEmail.setText(ApplicationClass.user.getEmail());

        //
        //Name      : onClick for return button
        //Purpose   : Returns user to the main activity.
        //

        profileReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                Profile.this.finish();
            }
        });

        //
        //Name      : onClick for Profile Deactivation
        //Purpose   : Deactivates a user profile.
        //

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
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        Profile.this.finish();
    }//end method
}