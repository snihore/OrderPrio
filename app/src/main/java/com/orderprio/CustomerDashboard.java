package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class CustomerDashboard extends AppCompatActivity implements View.OnClickListener {

    ImageView qrScannerCode, customerLogoutBtn;
    public static String SHOP_SCANNED_ID = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        mAuth = FirebaseAuth.getInstance();
        initViews();

        //click events
        qrScannerCode.setOnClickListener(this);
        customerLogoutBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SHOP_SCANNED_ID != null){
            Toast.makeText(this, SHOP_SCANNED_ID, Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {

        qrScannerCode = (ImageView)findViewById(R.id.qr_scan_btn);
        customerLogoutBtn = (ImageView)findViewById(R.id.customer_logout_btn);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.qr_scan_btn:
                scanShopQr();
                break;
            case R.id.customer_logout_btn:
                logoutOps();
                break;
        }
    }

    private String getUser(){
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(this, "User not found ...", Toast.LENGTH_SHORT).show();
            return null;
        }
        String customer = null;
        if(mAuth.getCurrentUser().getEmail() != null && !mAuth.getCurrentUser().getEmail().matches("")){
            customer = mAuth.getCurrentUser().getEmail();
        }
        if(mAuth.getCurrentUser().getPhoneNumber() != null && !mAuth.getCurrentUser().getPhoneNumber().matches("")){
            customer = mAuth.getCurrentUser().getPhoneNumber();
        }
        if(customer.matches("")){
            Toast.makeText(this, "User not found ...", Toast.LENGTH_SHORT).show();
            return null;
        }
        return customer;
    }

    private void logoutOps() {
        if(getUser() != null){
            mAuth.signOut();

        }
        startActivity(
                new Intent(getApplicationContext(), MainActivity.class)
        );
        finish();
        Toast.makeText(this, "Customer Logout", Toast.LENGTH_SHORT).show();
    }

    private void scanShopQr() {
        startActivity(
                new Intent(getApplicationContext(), ScanQRActivity.class)
        );
    }
}
