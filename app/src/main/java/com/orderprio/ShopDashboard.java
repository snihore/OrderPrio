package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ShopDashboard extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout addMenuBtn, qrCodeBtn, settingsBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_dashboard);

        mAuth = FirebaseAuth.getInstance();
        initViews();

        //click events ...
        addMenuBtn.setOnClickListener(this);
        qrCodeBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);

    }

    private void initViews() {
        addMenuBtn = (LinearLayout)findViewById(R.id.add_menu_layout_btn);
        qrCodeBtn = (LinearLayout)findViewById(R.id.qr_code_layout_btn);
        settingsBtn = (LinearLayout)findViewById(R.id.settings_layout_btn);
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()){

            case R.id.add_menu_layout_btn :
                intent = new Intent(getApplicationContext(), AddMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.qr_code_layout_btn:
                intent = new Intent(getApplicationContext(), ShopQRActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_layout_btn:
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
        Toast.makeText(this, "Shop Logout", Toast.LENGTH_SHORT).show();
    }
}

// 1. Add Today's Menu
// 2. Generate QR Code
// 3. Logout
// 4. Get Orders At Realtime

//Add Menu
// 1. date
// 2. cuisine name
// 3. cuisine price
// 4. availability
