package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orderprio.data.ShopMenuData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText cuisineNameEditText, cuisinePriceEditText;
    private Button addBtn;
    private TextView addStatus;

    public static final String SHOP_MENU_KEY = "SHOP_MENU_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        initViews();

        //click events ...
        addBtn.setOnClickListener(this);
    }

    private void initViews() {
        addStatus = (TextView)findViewById(R.id.add_status);
        cuisineNameEditText = (EditText)findViewById(R.id.menu_cuisine_name);
        cuisinePriceEditText = (EditText)findViewById(R.id.menu_cuisine_price);
        addBtn = (Button)findViewById(R.id.menu_add_btn);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.menu_add_btn:
                addCuisine();
                break;
        }
    }

    private void addCuisine() {
        String cuisineName = cuisineNameEditText.getText().toString().trim();
        String cuisinePrice = cuisinePriceEditText.getText().toString().trim();

        if(cuisineName.matches("") || cuisinePrice.matches("")){
            Toast.makeText(this, "Please enter cuisine name and price correctly ...", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collection = FirebaseFirestore.getInstance().collection("ShopMenuData");
        DocumentReference documentReference;
        String dateStr;
       try{
           // Get Current Date ...
           Date date = Calendar.getInstance().getTime();
           SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
           dateStr = dateFormat.format(date);

           //Add data to FireStore
           documentReference = collection.document(dateStr);

       }catch (Exception e){
           documentReference = collection.document();
       }

       final ShopMenuData shopMenuData = new ShopMenuData(cuisineName, cuisinePrice, true);

        Map<String, ShopMenuData> map = new HashMap<>();
        map.put(SHOP_MENU_KEY, shopMenuData);

        addStatus.setText("wait...");
        documentReference.collection("ItemList").document(shopMenuData.getCuisineName()).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddMenuActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        addStatus.setText("added "+shopMenuData.getCuisineName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddMenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        addStatus.setText("not added "+shopMenuData.getCuisineName());
                    }
                });

    }
}
