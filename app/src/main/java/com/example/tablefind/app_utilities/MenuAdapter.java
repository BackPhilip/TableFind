package com.example.tablefind.app_utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tablefind.R;
import com.example.tablefind.data_models.RestaurantMenuItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MenuAdapter extends ArrayAdapter<RestaurantMenuItem>
{
    private Context context;
    private List<RestaurantMenuItem> dishes;

    public MenuAdapter(Context context, List<RestaurantMenuItem> list) {

        super(context, R.layout.menu_row_layout, list);
        this.context = context;
        this.dishes = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.menu_row_layout, parent, false);

        final ImageView dishImage = convertView.findViewById(R.id.menuImage);
        TextView dishInfo = convertView.findViewById(R.id.dishInfo);
        TextView dishAvailable = convertView.findViewById(R.id.dishAvailable);

        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("dishes/" + dishes.get(position).getObjectId() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                dishImage.setImageBitmap(ApplicationClass.getRoundedCornerBitmap(bm,100));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        dishInfo.setText(dishes.get(position).getName().trim());

        if (!(dishes.get(position).getOutOfStock()))
        {
            dishAvailable.setText("A");
            dishAvailable.setTextColor(Color.GREEN);
        }
        else
        {
            dishAvailable.setText("U");
            dishAvailable.setTextColor(Color.RED);
        }

        return convertView;
    }
}
