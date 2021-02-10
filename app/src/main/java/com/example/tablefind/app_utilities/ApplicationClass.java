package com.example.tablefind.app_utilities;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.tablefind.R;
import com.example.tablefind.data_models.Reservation;
import com.example.tablefind.data_models.Restaurant;
import com.example.tablefind.data_models.RestaurantMenuItem;
import com.example.tablefind.data_models.RestaurantTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationClass extends Application
{
    public static final String APPLICATION_ID = "6D59291D-64B4-B4E5-FFCD-43BA19198A00";
    public static final String API_KEY = "E77A2EEA-6522-47FE-9B3F-EBAB012C6961";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static ArrayList<Restaurant> restaurants = new ArrayList<>();
    public static Restaurant restaurant;
    public static ArrayList<RestaurantTable> tables = new ArrayList<>();
    public static RestaurantTable table;
    public static ArrayList<Reservation> reservations = new ArrayList<>();
    public static Reservation reservation;
    public static ArrayList<RestaurantMenuItem> menuItems = new ArrayList<>();
    public static RestaurantMenuItem menuItem;

    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(), APPLICATION_ID, API_KEY );
    }

    public static String EmailValidation( String email)
    {
        String message=" ";
        String regex ="^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()==false)
        {
            message="Email is incorrectly formatted";
        }
        else
        {
            message="";
        }


        return message;
    }//end method


    public static String PasswordValidation(String password)
    {
        String message=" ";
        String regex ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()==false)
        {
            message="Password is too simple!";
        }
        else
        {
            message="";
        }
        return message;
    }//end method

    public static String PasswordValidation(String password1,String password2)
    {
        String message;
        if(password1.equals(password2))
        {
            message="";
        }
        else
        {
            message ="Passwords does not match";
        }
        return  message;
    }

    public static String PhoneNumberValidation(String phoneNumber)
    {
        String message=" ";
        String regex ="^(\\(0\\d\\d\\)\\s\\d{3}[\\s-]+\\d{4})|(0\\d\\d[\\s-]+\\d{3}[\\s-]+\\d{4})|(0\\d{9})|(\\+\\d\\d\\s?[\\(\\s]\\d\\d[\\)\\s]\\s?\\d{3}[\\s-]?\\d{4})$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        if(matcher.matches()==false)
        {
            message=phoneNumber+" is not a correctly formatted phone number";
        }
        else
        {
            message="";
        }
        return message;
    }//end method
    public static void showToast(String message, int type, Activity context) {
        //info toasts
        View toastView = context.getLayoutInflater().inflate(R.layout.toast, (ViewGroup) context.findViewById((R.id.linlay)));
        ImageView ivToast = toastView.findViewById(R.id.ivToast);
        TextView tvMessage = toastView.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        if (type == 2)
        {
            ivToast.setImageResource(R.drawable.info);
        }
        toast.show();
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
