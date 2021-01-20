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

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationClass extends Application
{
    public static final String APPLICATION_ID = "3341DD88-C207-2A48-FF9F-D4103CEA4900";
    public static final String API_KEY = "7E8D50A1-FA22-4605-BBD0-8AFAC88ED24E";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Restaurant> restaurants;
    public static Restaurant restaurant;
    public static List<RestaurantTable> tables;
    public static RestaurantTable table;
    public static List<Reservation> reservations;
    public static Reservation reservation;
    public static List<RestaurantMenuItem> menuItems;
    public static RestaurantMenuItem menuItem;

    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(), APPLICATION_ID, API_KEY );

        Restaurant restaurant = new Restaurant();
        restaurant.setLocationString("San Francisco");
        restaurant.setLocationGPS("geo:37.7749,-122.4194");
        restaurant.setName("Stadium");
        restaurant.setContactNumber("0713797679");
        restaurant.setMenuLink("");
        restaurant.setOwnerId("C3D37EF1-4A5C-402F-87B0-F04FF6AC1B59");

        /*Backendless.Persistence.save(restaurant, new AsyncCallback<Restaurant>() {
            @Override
            public void handleResponse(Restaurant response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });*/

        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setAvailable(true);
        restaurantTable.setCapacity(4);
        restaurantTable.setRestaurantId("52BD5D08-F6B2-4D5E-8C37-E20420F7EC0F");
        restaurantTable.setTableInfo("Close to Bathroom");
        restaurantTable.setxPos(0);
        restaurantTable.setyPos(0);

        /*Backendless.Persistence.save(restaurantTable, new AsyncCallback<RestaurantTable>() {
            @Override
            public void handleResponse(RestaurantTable response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });*/

        Reservation reservation = new Reservation();
        reservation.setRestaurantId("52BD5D08-F6B2-4D5E-8C37-E20420F7EC0F");
        reservation.setUserId("F5772669-9336-4393-8DD8-4C4B6BB81CA0");
        Calendar calendar = Calendar.getInstance();
        reservation.setTakenTo(calendar.getTime());
        reservation.setTakenFrom(calendar.getTime());
        reservation.setTableId("21E0A3DE-ECED-43BD-852D-9DBEAC4D8590");
        reservation.setName("Philip");
        reservation.setNumber("0713797679");
        /*Backendless.Persistence.save(reservation, new AsyncCallback<Reservation>() {
            @Override
            public void handleResponse(Reservation response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });*/

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

    public static String IDnumberValidation(String idNumber)
    {
        String message=" ";
        String regex ="^(((\\d{2}((0[13578]|1[02])(0[1-9]|[12]\\d|3[01])|(0[13456789]|1[012])(0[1-9]|[12]\\d|30)|02(0[1-9]|1\\d|2[0-8])))|([02468][048]|[13579][26])0229))(( |-)(\\d{4})( |-)([01]8((( |-)\\d{1})|\\d{1}))|(\\d{4}[01]8\\d{1}))$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher = pattern.matcher(idNumber);

        if(matcher.matches()==false)
        {
            message=idNumber+" is not a valid SA id number";
        }
        else
        {
            message="";
        }
        return  message;
    }//end method
    public static void showToast(String message, int type, Activity context) {
        //info toasts
        View toastView = context.getLayoutInflater().inflate(R.layout.toast, (ViewGroup) context.findViewById((R.id.linlay)));
        ImageView ivToast = toastView.findViewById(R.id.ivToast);
        TextView tvMessage = toastView.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
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
