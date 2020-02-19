package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        startSplash();
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

    private void findShop() {
        if(getUser() == null){
            return;
        }
        FirebaseFirestore
                .getInstance()
                .collection("OrderPrio")
                .document("Shop")
                .collection(getUser()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), EnterActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    if(queryDocumentSnapshots.getDocuments().isEmpty()){
                        //Not Shop ...
                        //Find Customer ...
                        findCustomer();
                    }else{
                        // Shop ...
                        Intent intent = new Intent(getApplicationContext(), ShopDashboard.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
    private void findCustomer() {
        if(getUser() == null){
            return;
        }
        FirebaseFirestore
                .getInstance()
                .collection("OrderPrio")
                .document("Customer")
                .collection(getUser()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), EnterActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    if(queryDocumentSnapshots.getDocuments().isEmpty()){
                        //Not Customer ...
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), EnterActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        // Customer ...
                        Intent intent = new Intent(getApplicationContext(), CustomerDashboard.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    private void startSplash() {
        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(getUser() == null){
                    Intent intent = new Intent(getApplicationContext(), EnterActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    findShop();
                }

            }

        };
        countDownTimer.start();
    }


}
