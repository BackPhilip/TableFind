package com.example.tablefind.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tablefind.R;
import com.example.tablefind.app_utilities.ApplicationClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/// <summary>
/// Shows details of selected menu item.
/// </summary>

public class MenuItemDetail extends AppCompatActivity {

    ImageView menuDetailImage;
    TextView dishDetailName, dishDetailType, dishDetailAllergens, dishDetailPrice;
    Button dishDetailBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_detail);
        setTitle("Details");

        /// Initialization vvv ///
        menuDetailImage = findViewById(R.id.menuDetailImage);

        dishDetailName = findViewById(R.id.dishDetailName);
        dishDetailType = findViewById(R.id.dishDetailType);
        dishDetailAllergens = findViewById(R.id.dishDetailAllergens);
        dishDetailPrice = findViewById(R.id.dishDetailPrice);

        dishDetailBtn = findViewById(R.id.dishDetailBtn);

        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("dishes/" + ApplicationClass.menuItem.getObjectId() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                menuDetailImage.setImageBitmap(ApplicationClass.getRoundedCornerBitmap(bm,100));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        dishDetailName.setText(ApplicationClass.menuItem.getName());
        dishDetailType.setText(ApplicationClass.menuItem.getType());
        dishDetailAllergens.setText(ApplicationClass.menuItem.getIngredients());
        dishDetailPrice.setText("R " + ApplicationClass.menuItem.getPrice());

        dishDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuItemDetail.this, Menu.class);
                startActivity(intent);
                MenuItemDetail.this.finish();
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
        Intent intent = new Intent(MenuItemDetail.this, Menu.class);
        startActivity(intent);
        MenuItemDetail.this.finish();
    }//end method
}