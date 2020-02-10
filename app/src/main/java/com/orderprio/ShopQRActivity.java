package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orderprio.data.QRCodeData;
import com.orderprio.data.ShopRegistrationData;

import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ShopQRActivity extends AppCompatActivity implements View.OnClickListener {

    private Button generateQRBtn;
    private ImageView shopQRCodeImg;

    private FirebaseAuth mAuth;
    private DocumentReference documentReference = FirebaseFirestore.getInstance().collection("OrderPrio").document("QRCodes");

    private QRGEncoder qrgEncoder;
    private Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_qr);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        generateAndDisplayQRCode();

        //click events ...
        generateQRBtn.setOnClickListener(this);

    }

    private void initViews() {
        generateQRBtn = (Button)findViewById(R.id.generate_qr_btn);
        shopQRCodeImg = (ImageView)findViewById(R.id.shop_qr_code_img);
    }

    public String createTransactionID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.generate_qr_btn:
                generate();
                break;
        }
    }

    private void generateQR(String text){
        WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width<height ? width:height;
        smallerDimension = smallerDimension * 3/4;
        qrgEncoder = new QRGEncoder(
                text,
                null,
                QRGContents.Type.TEXT,
                smallerDimension
        );
        try{
            qrBitmap = qrgEncoder.encodeAsBitmap();
            shopQRCodeImg.setImageBitmap(qrBitmap);

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void generateAndDisplayQRCode(){

        if(mAuth.getCurrentUser() == null){
            Toast.makeText(this, "Shop User not find ...", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        String email = mAuth.getCurrentUser().getEmail();

        String shopID = null;
        if(phoneNumber != null && !phoneNumber.matches("")){
            shopID = phoneNumber;
        }
        if(email != null && !email.matches("")){
            shopID = email;
        }

        if(shopID == null){
            Toast.makeText(this, "Shop Account not find ...", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore.getInstance()
                .collection("OrderPrio")
                .document("Shop")
                .collection(shopID)
                .document("RegistrationData")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ShopRegistrationData shopRegistrationData = documentSnapshot.toObject(ShopRegistrationData.class);
                Log.i("SHOP_REGISTRATION_DATA", shopRegistrationData.getShopID());

                //Create and Display QR Code ...
                String text = shopRegistrationData.getUniqueID();

                if(text.matches("")){
                    generateQRBtn.setText("Generate");
                }else{
                    generateQR(text);
                    generateQRBtn.setText("Re-generate");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShopQRActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

    }
    private void generate() {
        try{

            final String uniqueShopID = createTransactionID();
            Log.i("UNIQUE SHOP ID", uniqueShopID);

            if(mAuth.getCurrentUser() == null){
                Toast.makeText(this, "Shop User not find ...", Toast.LENGTH_SHORT).show();
                return;
            }

            String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
            String email = mAuth.getCurrentUser().getEmail();

            String shopID = null;
            if(phoneNumber != null && !phoneNumber.matches("")){
                shopID = phoneNumber;
            }
            if(email != null && !email.matches("")){
                shopID = email;
            }

            if(shopID == null){
                Toast.makeText(this, "Shop Account not find ...", Toast.LENGTH_SHORT).show();
                return;
            }

            //GET ShopRegistrationData ...

            final String finalShopID = shopID;
            FirebaseFirestore.getInstance()
                    .collection("OrderPrio")
                    .document("Shop")
                    .collection(shopID)
                    .document("RegistrationData")
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ShopRegistrationData shopRegistrationData = documentSnapshot.toObject(ShopRegistrationData.class);
                    Log.i("SHOP_REGISTRATION_DATA", shopRegistrationData.getShopID());

                    //UPDATE ShopRegistrationData ...
                    updateShopRegistrationData(finalShopID, uniqueShopID, shopRegistrationData);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShopQRActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addUniqueIdToFirestore(String shopID, String uniqueShopID) {

        QRCodeData qrCodeData = new QRCodeData(shopID, uniqueShopID);

        documentReference.collection(uniqueShopID).document().set(qrCodeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShopQRActivity.this, "QR generated", Toast.LENGTH_SHORT).show();
                        generateAndDisplayQRCode();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShopQRActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }

    private void updateShopRegistrationData(final String shopID, final String uniqueShopID, ShopRegistrationData shopRegistrationData){

        shopRegistrationData.setUniqueID(uniqueShopID);

        FirebaseFirestore.getInstance()
                .collection("OrderPrio")
                .document("Shop")
                .collection(shopID)
                .document("RegistrationData")
                .set(shopRegistrationData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //NOW, set UNIQUE ID for customers ...
                        addUniqueIdToFirestore(shopID, uniqueShopID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShopQRActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }
}
