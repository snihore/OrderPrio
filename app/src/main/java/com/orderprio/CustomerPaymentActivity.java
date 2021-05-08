package com.orderprio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orderprio.data.ShopPaymentData;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerPaymentActivity extends AppCompatActivity {

    private ArrayList<String> proceedData;
    private HashMap<String, String> hashMap = new HashMap<>();

    private TextView amountText, nameText, upiIDText;
    private Button payBtn;

    private static final int UPI_PAYMENT = 786;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);

        initViews();

        try{
            proceedData = getIntent().getStringArrayListExtra("PROCEED_DATA");

            if(proceedData.size()>0){
                hashMap.put("shopID", proceedData.get(0));
                hashMap.put("amount", proceedData.get(1));

                getMerchantsUPIDetails(hashMap.get("shopID"));

            }else{
                Toast.makeText(this, "Not Proceed !!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payUsingUPI();
            }
        });
    }

    private void initViews() {

        payBtn = (Button)findViewById(R.id.final_pay_btn);
        nameText = (TextView)findViewById(R.id.final_upi_name_text);
        upiIDText = (TextView)findViewById(R.id.final_upi_id_text);
        amountText = (TextView)findViewById(R.id.final_amount_text);
    }

    private void getMerchantsUPIDetails(final String shopID) {
        if(shopID == null){
            Toast.makeText(this, "Not Proceed, Shop not found", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }else{
            try{
                FirebaseFirestore
                        .getInstance()
                        .collection("OrderPrio")
                        .document("Shop")
                        .collection(shopID)
                        .document("ShopPaymentData")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ShopPaymentData shopPaymentData = documentSnapshot.toObject(ShopPaymentData.class);
                                if(shopPaymentData != null && shopPaymentData.getShopID().matches(shopID)){
                                    hashMap.put("upi_name", shopPaymentData.getUpiName());
                                    hashMap.put("upi_id", shopPaymentData.getUpiID());

                                    Log.i("PROCEED_DATA", hashMap.toString());
                                    nameText.setText(hashMap.get("upi_name"));
                                    upiIDText.setText(hashMap.get("upi_id"));
                                    amountText.setText("\u20B9 "+hashMap.get("amount"));
                                    payBtn.setText("Pay");
                                }else {
                                    Toast.makeText(getApplicationContext(), "Not Proceed, Shop ID not matched", Toast.LENGTH_SHORT).show();
                                    onBackPressed();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                onBackPressed();

            }
        }
    }

    private void payUsingUPI(){
//        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
//        int GOOGLE_PAY_REQUEST_CODE = 123;

        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", hashMap.get("upi_id"))
                        .appendQueryParameter("pn", hashMap.get("upi_name"))
//                        .appendQueryParameter("mc", "your-merchant-code")
//                        .appendQueryParameter("tr", "your-transaction-ref-id")
                        .appendQueryParameter("tn", "Pay for Cuisines")
                        .appendQueryParameter("am", hashMap.get("amount"))
                        .appendQueryParameter("cu", "INR")
//                        .appendQueryParameter("url", "your-transaction-url")
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
//        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
//        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

        //Will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(intent, "Pay Amount from ");

        //Check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser, UPI_PAYMENT);
        }else{
            Toast.makeText(this, "No UPI App found ...", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("UPI_RESULT_CODE", resultCode+"");

        Log.i("UPI_PAYMENT_CODE", requestCode+"");

        switch (requestCode){
            case UPI_PAYMENT:
                try{
                    String transaction = data.getStringExtra("response");
                    Log.i("UPI_TRANSACTION", transaction);
                    Toast.makeText(this, "Payment Successfull", Toast.LENGTH_SHORT).show();

                    try{
                        String[] arr = transaction.split("&");
                        String status = arr[2];
                        String[] arr2 = status.split("=");
                        if(arr2[1].matches("FAILURE")){
                            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                        }else if(arr2[1].matches("SUCCESS")){
                            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this, "Payment is in Trouble", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    onBackPressed();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
        }
    }
}
