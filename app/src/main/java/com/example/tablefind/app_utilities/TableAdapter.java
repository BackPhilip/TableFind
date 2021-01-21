package com.example.tablefind.app_utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import static com.example.tablefind.R.*;

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
        convertView = inflater.inflate(layout.table_row_layout, parent, false);

        TextView tableInfo = convertView.findViewById(id.tableInfo);
        TextView tableAvailable = convertView.findViewById(id.tableAvailable);
        final ImageView tableImage = convertView.findViewById(id.tableImage);

        switch (tables.get(position).getCapacity())
        {
            case 1:
                tableImage.setImageResource(mipmap.one_foreground);
                break;
            case 2:
                tableImage.setImageResource(mipmap.two_foreground);
                break;
            case 3:
                tableImage.setImageResource(mipmap.three_foreground);
                break;
            case 4:
                tableImage.setImageResource(mipmap.four_foreground);
                break;
            case 5:
                tableImage.setImageResource(mipmap.five_foreground);
                break;
            case 6:
                tableImage.setImageResource(mipmap.six_foreground);
                break;
            case 7:
                tableImage.setImageResource(mipmap.seven_foreground);
                break;
            case 8:
                tableImage.setImageResource(mipmap.eight_foreground);
                break;
        }

        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                tableInfo.setTextColor(Color.MAGENTA);
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
