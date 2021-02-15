package com.example.tablefind.app_utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tablefind.R;
import com.example.tablefind.data_models.Restaurant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RestaurantAdapter extends BaseAdapter
{
    private Context context;
    private List<Restaurant> restaurants;
    LayoutInflater inflater;

    public RestaurantAdapter(Context context, List<Restaurant> list)
    {
        this.context = context;
        this.restaurants = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_layout, parent, false);

        final ImageView restaurantImage = convertView.findViewById(R.id.restaurantImage);
        TextView restaurantName = convertView.findViewById(R.id.restaurantName);
        TextView restaurantLocation = convertView.findViewById(R.id.restaurantLocation);
        ImageView capacityDot = convertView.findViewById(R.id.capacityDot);

        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("restaurants/" + restaurants.get(position).getName() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;

            mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    restaurantImage.setImageBitmap(ApplicationClass.getRoundedCornerBitmap(bm, 100));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    restaurantImage.setImageResource(R.mipmap.tablefind_foreground);
                }
            });
        restaurantName.setText(restaurants.get(position).getName().trim());
        restaurantLocation.setText(restaurants.get(position).getLocationString());

        if (restaurants.get(position).getMaxCapacity() == 0)
        {
            capacityDot.setColorFilter(Color.GREEN);
        }
        else if (restaurants.get(position).getMaxCapacity() == 1)
        {
            capacityDot.setColorFilter(Color.YELLOW);
        }
        else if (restaurants.get(position).getMaxCapacity() == 2)
        {
            capacityDot.setColorFilter(Color.RED);
        }
        return convertView;
    }
}
