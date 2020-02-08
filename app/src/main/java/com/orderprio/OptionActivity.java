package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        findViewById(R.id.go_to_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OptionActivity.this, "Shop", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ShopRegistration.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.go_to_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OptionActivity.this, "Customer", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
