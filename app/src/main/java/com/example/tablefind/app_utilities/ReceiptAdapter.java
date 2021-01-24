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

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.tablefind.R;
import com.example.tablefind.data_models.Reservation;
import com.example.tablefind.data_models.Restaurant;
import com.example.tablefind.data_models.RestaurantMenuItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ReceiptAdapter extends ArrayAdapter<Reservation>
{
    private Context context;
    private List<Reservation> reservations;

    public ReceiptAdapter(Context context, List<Reservation> list) {

        super(context, R.layout.receipt_row_layout, list);
        this.context = context;
        this.reservations = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.receipt_row_layout, parent, false);

        final ImageView receiptImage = convertView.findViewById(R.id.receiptImage);
        final TextView receiptInfo = convertView.findViewById(R.id.receiptInfo);

        String whereClause = "objectId = '" + reservations.get(position).getRestaurantId() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Persistence.of(Restaurant.class).find(queryBuilder, new AsyncCallback<List<Restaurant>>() {
            @Override
            public void handleResponse(List<Restaurant> response) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference("restaurants/" + response.get(0).getName() + ".jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        receiptImage.setImageBitmap(ApplicationClass.getRoundedCornerBitmap(bm,100));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                receiptInfo.setText(response.get(0).getName() + "\n" + response.get(0).getContactNumber());
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        return convertView;
    }
}
