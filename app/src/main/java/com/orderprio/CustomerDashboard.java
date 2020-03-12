package com.orderprio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.orderprio.data.Cart;
import com.orderprio.data.QRCodeData;
import com.orderprio.data.ShopMenuData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class CustomerDashboard extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RelativeLayout goToCartBtn;
    private ImageView qrScannerCode, customerLogoutBtn;
    private TextView showShopNameTextView, cartItemTotal;
    private ListView listView;
    public static String SHOP_SCANNED_ID = null;
    private AlertDialog cartDialog;

    private FirebaseAuth mAuth;
    private ListenerRegistration listenerRegistration;

    private Cart cartData = new Cart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        mAuth = FirebaseAuth.getInstance();
        initViews();

        //click events
        qrScannerCode.setOnClickListener(this);
        customerLogoutBtn.setOnClickListener(this);
        goToCartBtn.setOnClickListener(this);

        //ListView Item Clicks
        listView.setOnItemClickListener(this);

    }

    private void initViews() {

        qrScannerCode = (ImageView)findViewById(R.id.qr_scan_btn);
        customerLogoutBtn = (ImageView)findViewById(R.id.customer_logout_btn);
        listView = (ListView)findViewById(R.id.cust_dash_show_items_list);
        showShopNameTextView = (TextView)findViewById(R.id.cust_dash_show_shop_name);
        cartItemTotal = (TextView)findViewById(R.id.cart_item_total);
        goToCartBtn = (RelativeLayout)findViewById(R.id.go_to_cart_btn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SHOP_SCANNED_ID != null){

            listView.setVisibility(View.VISIBLE);
            getShopID(SHOP_SCANNED_ID);
        }else{
            listView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SHOP_SCANNED_ID = null;
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
    }

    private void getShopID(String id) {
        listenerRegistration = FirebaseFirestore
                .getInstance()
                .collection("OrderPrio")
                .document("QRCodes")
                .collection(id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Toast.makeText(CustomerDashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            if(queryDocumentSnapshots.isEmpty()){
                                Toast.makeText(CustomerDashboard.this, "Shop Not found ...", Toast.LENGTH_SHORT).show();
                                showShopNameTextView.setText("Shop Not Found");
                            }else{
                                QRCodeData qrCodeData = queryDocumentSnapshots.getDocuments().get(0).toObject(QRCodeData.class);

                                //Get Shop's Food Items ...
                                if(qrCodeData != null && qrCodeData.getShopID() != null && !qrCodeData.getShopID().matches("")){
                                    getScannedShopData(qrCodeData.getShopID());
                                }else{
                                    Toast.makeText(CustomerDashboard.this, "Shop Not Found ...", Toast.LENGTH_SHORT).show();
                                    showShopNameTextView.setText("Shop Not Found");
                                }
                            }
                        }
                    }
                });
    }

    private void getScannedShopData(String shopID) {

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if(date == null){
            date = "ItemList";
        }

        FirebaseFirestore
                .getInstance()
                .collection("OrderPrio")
                .document("Shop")
                .collection(shopID)
                .document("MenuData")
                .collection(date).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(CustomerDashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(CustomerDashboard.this, "Food Items Not Found ...", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                List<ShopMenuData> itemList = new ArrayList<>();
                for(DocumentSnapshot snapshot: list){
                    try{
                        ShopMenuData shopMenuData = snapshot.toObject(ShopMenuData.class);
                        itemList.add(shopMenuData);
                    }catch (Exception err){
                        Toast.makeText(CustomerDashboard.this, err.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.qr_scan_btn:
                scanShopQr();
                break;
            case R.id.customer_logout_btn:
                logoutOps();
                break;
            case R.id.go_to_cart_btn:
                Toast.makeText(this, "Go to Cart", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                try{
                    intent.putExtra("CART_DATA", cartData);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(this, "ITEM "+i, Toast.LENGTH_SHORT).show();

        addItemToCart(view);
    }

    private void addItemToCart(View view) {
        TextView cuisineName = (TextView) view.findViewById(R.id.listview_item_cuisine_name);
        TextView cuisinePrice = (TextView) view.findViewById(R.id.listview_item_cuisine_price);

        cartDialogBox(cuisineName.getText().toString(), cuisinePrice.getText().toString());
        cartDialog.show();
    }

    private void cartDialogBox(final String name, String price) {
        LayoutInflater factory = LayoutInflater.from(CustomerDashboard.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog04, null);

        //Views ...
        TextView cartCuisineName = dialogView.findViewById(R.id.cart_cuisine_name);
        final TextView cartCuisinePrice = dialogView.findViewById(R.id.cart_cuisine_price);
        final TextView cartCuisineQuantity = dialogView.findViewById(R.id.cart_cuisine_quantity);
        ImageButton cartCuisineAdd = dialogView.findViewById(R.id.cart_cuisine_add);
        ImageButton cartCuisineSub = dialogView.findViewById(R.id.cart_cuisine_sub);
        ImageButton cartCuisineDone = dialogView.findViewById(R.id.cart_cuisine_done);

        final int[] cuisneQuantity = {1};
        final int[] cusinePrice = {Integer.parseInt(price.substring(2))};
        final int basePrice = Integer.parseInt(price.substring(2));


        //Set Values
        cartCuisineName.setText(name);
        cartCuisineQuantity.setText(String.valueOf(cuisneQuantity[0]));
        cartCuisinePrice.setText("\u20B9 "+String.valueOf(cusinePrice[0]));

        cartCuisineSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cuisneQuantity[0]>1){
                    cuisneQuantity[0] -= 1;
                    cartCuisineQuantity.setText(String.valueOf(cuisneQuantity[0]));

                    cusinePrice[0] -= basePrice;
                    cartCuisinePrice.setText("\u20B9 "+String.valueOf(cusinePrice[0]));


                }
            }
        });
        cartCuisineAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cuisneQuantity[0] += 1;
                cartCuisineQuantity.setText(String.valueOf(cuisneQuantity[0]));

                cusinePrice[0] += basePrice;
                cartCuisinePrice.setText("\u20B9 "+String.valueOf(cusinePrice[0]));
            }
        });

        cartCuisineDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartData.addCuisineName(name);
                cartData.addCuisinePrice(name, cusinePrice[0]);
                cartData.addCuisineQuantity(name, cuisneQuantity[0]);
                cartItemTotal.setVisibility(View.VISIBLE);
                cartItemTotal.setText(String.valueOf(cartData.getTotalItems()));
                Toast.makeText(CustomerDashboard.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                Log.i("CART_QUANTITY", cartData.getCuisineQuantities().toString());
                Log.i("CART_PRICES", cartData.getCuisinePrices().toString());
                cartDialog.cancel();
            }
        });

        cartDialog = new AlertDialog.Builder(CustomerDashboard.this).create();
        cartDialog.setView(dialogView);
    }


}
