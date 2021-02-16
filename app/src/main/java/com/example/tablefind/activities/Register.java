package com.example.tablefind.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.google.android.material.textfield.TextInputLayout;

/// <summary>
/// Activity to Register a new BackendlessUser
/// </summary>

public class Register extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText  edtMail, edtFirstName, edtLastName, edtPassword, edtConfirmPassword, edtCellNumber;
    TextInputLayout lytEmail, lytPassword, lytConfirmPassword, lytCellPhoneNumber, lytFirstName, lytLastName;
    Button btnRegister;
    ApplicationClass validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        /// Initialization vvv ///
        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        edtMail = findViewById(R.id.edtMail);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtCellNumber = findViewById(R.id.edtCellNumber);

        btnRegister = findViewById(R.id.btnRegister);

        lytEmail = findViewById(R.id.lytEmail);
        lytPassword = findViewById(R.id.lytPassword);
        lytConfirmPassword = findViewById(R.id.lytConfirmPassword);
        lytCellPhoneNumber = findViewById(R.id.lytCellphoneNumber);
        lytFirstName = findViewById(R.id.lytFirstName);
        lytLastName = findViewById(R.id.lytLastName);

        textBoxFocus();

        lytEmail.setHintAnimationEnabled(true);
        lytEmail.setHint("Email");
        //
        //Name             : onClick for Register button
        //Purpose          : checks if text boxes aren't empty and registers a new User if they aren't empty
        //
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(edtMail.getText().toString().trim().isEmpty() || edtFirstName.getText().toString().trim().isEmpty() || edtLastName.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty() ||
                        edtCellNumber.getText().toString().trim().isEmpty()) && Errorless())
                {
                    if (edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString().trim())) {
                        String FirstName = edtFirstName.getText().toString().trim();
                        String LastName = edtLastName.getText().toString().trim();
                        String email = edtMail.getText().toString().trim();
                        String password = edtPassword.getText().toString().trim();
                        String cellphone = edtCellNumber.getText().toString().trim();

                        final BackendlessUser user = new BackendlessUser();
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setProperty("FirstName", FirstName);
                        user.setProperty("LastName", LastName);
                        user.setProperty("Cellphone", cellphone);
                        user.setProperty("isOwner", false);

                        showProgress(true);
                        tvLoad.setText("Busy registering User...Please Wait...");

                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                ApplicationClass.showToast("Successfully Registered", 1, Register.this);

                                Backendless.UserService.resendEmailConfirmation(response.getEmail(), new AsyncCallback<Void>() {
                                    @Override
                                    public void handleResponse(Void response) {
                                        ApplicationClass.showToast("An Email Has Been Sent To You!", 1, Register.this);
                                        Register.this.finish();
                                        showProgress(false);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Register.this.finish();
                                        showProgress(false);
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                ApplicationClass.showToast(fault.toString().trim(), 2, Register.this);
                                showProgress(false);
                            }
                        });
                    } else {
                        ApplicationClass.showToast("Please make sure passwords match!", 2, Register.this);
                    }
                }
                else
                {
                    ApplicationClass.showToast("Please Confirm all Fields!", 2, Register.this);
                }
            }
        });
    }

    //
    //Method Name      : void showProgress()
    //Purpose          : Initialise and instantiate the progress bar and progress text
    //Re-use           : in onCreate()
    //Input Parameters : boolean show
    //Output Type      : void
    //
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
    }//end method

    //
    //Method Name      : void textBoxFocus()
    //Purpose          : Uses validation methods in Application class to ensure that the information given is in the correct format
    //Re-use           : in onCreate()
    //Input Parameters : none
    //Output Type      : void
    //
    private void textBoxFocus()
    {
        edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus==true)
                {

                    lytFirstName.setError(null);
                }
                else {
                    if (TextUtils.isEmpty(edtFirstName.getText().toString()))
                    {
                        lytFirstName.setError("First name can not be left empty");
                    }
                }
            }
        });

        edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus==true)
                {

                    lytLastName.setError(null);
                }
                else {
                    if (TextUtils.isEmpty(edtLastName.getText().toString())) {
                        lytLastName.setError("Last name can not be left empty");
                    }
                }
            }
        });

        lytEmail.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String email = lytEmail.getEditText().getText().toString().trim();

                if (lytEmail.getEditText().hasFocus())
                {
                    lytEmail.setError("");
                }
                else {
                    if(TextUtils.isEmpty(lytEmail.getEditText().getText().toString()))
                    {
                        lytEmail.setError("Email can not be left empty");
                    }
                    else {
                        lytEmail.setError(validate.EmailValidation(email));
                    }
                }
            }
        });

        lytPassword.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String password = lytPassword.getEditText().getText().toString().trim();

                if (lytPassword.getEditText().hasFocus())
                {
                    lytPassword.setError("");
                }
                else
                {
                    if (TextUtils.isEmpty(lytPassword.getEditText().getText().toString()))
                    {
                        lytPassword.setError("Password can not be left empty");
                    }
                    else
                    {
                        lytPassword.setError(validate.PasswordValidation(password));
                    }

                }
            }
        });

        lytConfirmPassword.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus==true)
                {

                    lytConfirmPassword.setError(null);
                }
                else {
                    if (TextUtils.isEmpty(edtConfirmPassword.getText().toString()))
                        lytConfirmPassword.setError("Confirm Password can not be left empty");
                    else {
                        lytConfirmPassword.setError(validate.PasswordValidation(edtPassword.getText().toString().trim(), edtConfirmPassword.getText().toString().trim()));
                    }
                }
            }
        });

        lytCellPhoneNumber.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String number = lytCellPhoneNumber.getEditText().getText().toString().trim();

                if (lytCellPhoneNumber.getEditText().hasFocus())
                {
                    lytCellPhoneNumber.setError("");
                }
                else
                    if(TextUtils.isEmpty(lytEmail.getEditText().getText().toString()))
                    lytCellPhoneNumber.setError("Phone number can not be left empty");
                else
                {
                    lytCellPhoneNumber.setError(validate.PhoneNumberValidation(number));
                }
            }
        });
    }//end method

    //
    //Method Name      : void Errorless()
    //Purpose          : Ensures that no errors are present when the user attempts to register
    //Re-use           : in onCreate()
    //Input Parameters : none
    //Output Type      : Boolean
    //
    private Boolean Errorless()
    {
        Boolean result = true;

        CharSequence error1 = lytEmail.getError();
        CharSequence error2 = lytFirstName.getError();
        CharSequence error3 = lytLastName.getError();
        CharSequence error4 = lytLastName.getError();
        CharSequence error5 = lytPassword.getError();
        CharSequence error6 = lytConfirmPassword.getError();
        CharSequence error7 = lytCellPhoneNumber.getError();

        if (error1 != null || error2 != null || error3 != null || error4 != null || error5 != null ||
                error6 != null || error7 != null)
            result=false;
        return result;
    }//end method
}