package com.example.tablefind.app_utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tablefind.R;
import com.example.tablefind.data_models.Restaurant;
import com.example.tablefind.data_models.RestaurantTable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TableAdapter extends BaseAdapter
{
    private Context context;
    private List<RestaurantTable> tables;

    public TableAdapter(Context context, List<RestaurantTable> list) {
        this.context = context;
        this.tables = list;
    }

    @Override
    public int getCount() {
        return tables.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.table_row_layout, parent, false);

        TextView tableInfo = convertView.findViewById(R.id.tableInfo);
        TextView tableAvailable = convertView.findViewById(R.id.tableAvailable);
        final ImageView tableImage = convertView.findViewById(R.id.tableImage);


        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("seatings/" + tables.get(position).getCapacity() + ".png");
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                tableImage.setImageBitmap(ApplicationClass.getRoundedCornerBitmap(bm,100));
                tableImage.setColorFilter(Color.BLACK);
                tableImage.setImageLevel(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                tableInfo.setTextColor(Color.YELLOW);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                tableInfo.setTextColor(Color.WHITE);
                break;
        }
        tableInfo.setText(tables.get(position).getTableInfo().trim());

        if (tables.get(position).isAvailable())
        {
            tableAvailable.setTextColor(Color.GREEN);
            tableAvailable.setText("A");
        }
        else
        {
            tableAvailable.setTextColor(Color.RED);
            tableAvailable.setText("U");
        }

        return convertView;
    }
}
