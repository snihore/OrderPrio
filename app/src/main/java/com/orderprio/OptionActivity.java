package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class OptionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private DocumentReference documentReference = FirebaseFirestore.getInstance().collection("OrderPrio").document("Customer");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        mAuth = FirebaseAuth.getInstance();
        customProgressBar();

        findViewById(R.id.go_to_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OptionActivity.this, "Shop", Toast.LENGTH_SHORT).show();
                findShop();
            }
        });
        findViewById(R.id.go_to_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OptionActivity.this, "Customer", Toast.LENGTH_SHORT).show();
                saveCustomerInfo();
            }
        });
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
        activateProgress(true);


        documentReference.collection(getUser()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    activateProgress(false);
                    e.printStackTrace();
                    Toast.makeText(OptionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    if(queryDocumentSnapshots.getDocuments().isEmpty()){
                        find();
                    }else{
                        activateProgress(false);
                        Toast.makeText(OptionActivity.this, "Entered Account is Customer Type ...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void find() {
        DocumentReference shopDocumentRef = FirebaseFirestore
                .getInstance()
                .collection("OrderPrio")
                .document("Shop");
        shopDocumentRef.collection(getUser()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                activateProgress(false);
                if(e != null){
//                    activateProgress(false);
                    Toast.makeText(OptionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    if(queryDocumentSnapshots.getDocuments().isEmpty()){
                        goToShopRegistration();
                    }else{
                        goToShopDashboard();
                    }
                }
            }
        });
    }

    private void customProgressBar(){
        LayoutInflater factory = LayoutInflater.from(OptionActivity.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog01, null);
        dialog = new AlertDialog.Builder(OptionActivity.this).create();
        dialog.setView(dialogView);
    }

    private void activateProgress(boolean flag){
        if(flag == true){
            dialog.show();
        }else{
            dialog.cancel();
        }
    }

    private void saveCustomerInfo() {
        if(getUser() == null) {
            return;
        }
        activateProgress(true);

        FirebaseFirestore
                .getInstance()
                .collection("OrderPrio")
                .document("Shop").collection(getUser()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    activateProgress(false);
                    e.printStackTrace();
                    Toast.makeText(OptionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    if(queryDocumentSnapshots.getDocuments().isEmpty()){
                        customerInfo();
                    }else{
                        activateProgress(false);
                        Toast.makeText(OptionActivity.this, "Entered Account is Shop Type ...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    private void customerInfo(){
        Map<String, String> map = new HashMap<>();
        documentReference.collection(getUser()).document("OrderHistory").set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activateProgress(false);
                        goToCustomerDashboard();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OptionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        activateProgress(false);
                    }
                });
    }

    private void goToCustomerDashboard() {
        Intent intent = new Intent(getApplicationContext(), CustomerDashboard.class);
        startActivity(intent);
        finish();
    }
    private void goToShopRegistration(){
        Intent intent = new Intent(getApplicationContext(), ShopRegistration.class);
        startActivity(intent);
        finish();
    }
    private void goToShopDashboard(){
        Intent intent = new Intent(getApplicationContext(), ShopDashboard.class);
        startActivity(intent);
        finish();
    }
}
