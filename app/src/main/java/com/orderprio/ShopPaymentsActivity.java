package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orderprio.data.ShopPaymentData;

public class ShopPaymentsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name, upiID;
    private Button saveBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_payments);

        mAuth = FirebaseAuth.getInstance();
        initViews();

        getShopPaymentData();

        //Click Events ...
        saveBtn.setOnClickListener(this);
    }

    private void getShopPaymentData() {
        if(getUser() != null){

            saveBtn.setText("Loading...");

            try{
                FirebaseFirestore
                        .getInstance()
                        .collection("OrderPrio")
                        .document("Shop")
                        .collection(getUser())
                        .document("ShopPaymentData")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ShopPaymentData shopPaymentData = documentSnapshot.toObject(ShopPaymentData.class);
                                if(shopPaymentData != null && shopPaymentData.getShopID().matches(getUser())){
                                    name.setText(shopPaymentData.getUpiName());
                                    upiID.setText(shopPaymentData.getUpiID());
                                    saveBtn.setText("Save");
                                }else {
                                    Toast.makeText(ShopPaymentsActivity.this, "Shop ID not matched", Toast.LENGTH_SHORT).show();
                                    saveBtn.setText("Save");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ShopPaymentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                saveBtn.setText("Save");
                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                saveBtn.setText("Save");
            }

        }

    }

    private String getUser(){
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(this, "User not found ...", Toast.LENGTH_SHORT).show();
            return null;
        }
        String shopID = null;
        if(mAuth.getCurrentUser().getEmail() != null && !mAuth.getCurrentUser().getEmail().matches("")){
            shopID = mAuth.getCurrentUser().getEmail();
        }
        if(mAuth.getCurrentUser().getPhoneNumber() != null && !mAuth.getCurrentUser().getPhoneNumber().matches("")){
            shopID = mAuth.getCurrentUser().getPhoneNumber();
        }
        if(shopID.matches("")){
            Toast.makeText(this, "User not found ...", Toast.LENGTH_SHORT).show();
            return null;
        }
        return shopID;
    }

    private void initViews() {

        saveBtn = (Button)findViewById(R.id.save_upi_btn);
        name = (EditText)findViewById(R.id.enter_upi_name);
        upiID = (EditText)findViewById(R.id.enter_upi_id);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.save_upi_btn:
                saveInfoUPI();
                break;
        }
    }

    private void saveInfoUPI() {

        if(name.getText().toString().matches("") || upiID.getText().toString().matches("")){
            Toast.makeText(this, "Please fill all the input fields ...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(getUser() != null){

            try{
                FirebaseFirestore
                        .getInstance()
                        .collection("OrderPrio")
                        .document("Shop")
                        .collection(getUser())
                        .document("ShopPaymentData")
                        .set(new ShopPaymentData(getUser(), upiID.getText().toString(), name.getText().toString()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(ShopPaymentsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                e.printStackTrace();
                                Toast.makeText(ShopPaymentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
