package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ShopDashboard extends AppCompatActivity implements View.OnClickListener {

    LinearLayout addMenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_dashboard);

        initViews();

        //click events ...
        addMenuBtn.setOnClickListener(this);

    }

    private void initViews() {
        addMenuBtn = (LinearLayout)findViewById(R.id.add_menu_layout_btn);
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()){

            case R.id.add_menu_layout_btn :
                intent = new Intent(getApplicationContext(), AddMenuActivity.class);
                startActivity(intent);
                break;
        }
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
