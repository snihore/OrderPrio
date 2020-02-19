package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.orderprio.data.ShopMenuData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AddMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText cuisineNameEditText, cuisinePriceEditText;
    private Button addBtn;
    private TextView addStatus;
    private ListView listView;
    FirebaseAuth mAuth;
    private DocumentReference documentReference = FirebaseFirestore.getInstance().collection("OrderPrio").document("Shop");
    private CollectionReference collectionReference;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        /*
        * cuisine name
        * cuisine price
        * cuisine ratings [ 5, 4, 3, 2, 1 star << peoples >> ]
        * total orders
        * */

        mAuth = FirebaseAuth.getInstance();

        initViews();

        fetchFromAuthUser();

        //click events ...
        addBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(collectionReference == null){
            return;
        }
        CollectionReference reference = collectionReference.document("MenuData").collection("ItemList");

        listenerRegistration = reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    e.printStackTrace();
                    return;
                }
                if(queryDocumentSnapshots.isEmpty()){
                    return;
                }
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                List<ShopMenuData> itemList = new ArrayList<>();
                for(DocumentSnapshot snapshot: list){
                    try{
                        ShopMenuData shopMenuData = snapshot.toObject(ShopMenuData.class);
                        itemList.add(shopMenuData);
                    }catch (Exception err){
                        err.printStackTrace();
                    }
                }

                if(itemList.size()>0){
                    CustomItemListView customItemListView = new CustomItemListView(getApplicationContext(), itemList);
                    listView.setAdapter(customItemListView);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
    }

    private void fetchFromAuthUser() {
        if(mAuth.getCurrentUser() != null){
            String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
            String email = mAuth.getCurrentUser().getEmail();

            String collectionName = null;

            if(phoneNumber != null){
                collectionName = phoneNumber;
            }
            if(email != null){
                collectionName = email;
            }

            if(collectionName != null){
                collectionReference = documentReference.collection(collectionName);
            }

        }
    }
    private void initViews() {
        addStatus = (TextView)findViewById(R.id.add_status);
        cuisineNameEditText = (EditText)findViewById(R.id.menu_cuisine_name);
        cuisinePriceEditText = (EditText)findViewById(R.id.menu_cuisine_price);
        addBtn = (Button)findViewById(R.id.menu_add_btn);
        listView = (ListView)findViewById(R.id.add_menu_listview);
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

        if(collectionReference == null){
            Toast.makeText(this, "Database error ...", Toast.LENGTH_SHORT).show();
            return;
        }

        addStatus.setText("wait...");

        DocumentReference reference = collectionReference.document("MenuData").collection("ItemList").document(cuisineName);

        ShopMenuData shopMenuData = new ShopMenuData(cuisineName, cuisinePrice, true);

        reference.set(shopMenuData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddMenuActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        addStatus.setText("added");
                        cuisineNameEditText.setText("");
                        cuisinePriceEditText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddMenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        addStatus.setText(e.getMessage());
                    }
                });
    }

}
