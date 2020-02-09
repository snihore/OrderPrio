package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orderprio.data.ShopRegistrationData;

import java.util.HashMap;
import java.util.Map;

public class ShopRegistration extends AppCompatActivity implements View.OnClickListener {

    private TextView stateTextView, countryTextView;
    private EditText shopNameEditText, addressEditText, emailEditText, mobileEditText, districtEditText, zipCodeEditText;
    private Button submitBtn;
    private AlertDialog stateDialogState, stateDialogCountry, dialog;
    private FirebaseAuth mAuth;

    public String SHOP_REGISTRATION_KEY = "SHOP_REGISTRATION_KEY";
    public String USER_NAME = "NO_USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_registration);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        customProgressBar();

        fetchFromAuthUser();

        //click events ...
        stateTextView.setOnClickListener(this);
        countryTextView.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    private void fetchFromAuthUser() {
        if(mAuth.getCurrentUser() != null){
            String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
            String email = mAuth.getCurrentUser().getEmail();

            if(phoneNumber != null){
                mobileEditText.setText(phoneNumber);
                USER_NAME = phoneNumber;
            }
            if(email != null){
                emailEditText.setText(email);
                USER_NAME = email;
            }
        }
    }

    private void initViews() {
        stateTextView = (TextView)findViewById(R.id.sr_state_textview);
        countryTextView = (TextView)findViewById(R.id.sr_country_textview);
        shopNameEditText = (EditText)findViewById(R.id.sr_shop_name_edittext);
        addressEditText = (EditText)findViewById(R.id.sr_address_edittext);
        emailEditText = (EditText)findViewById(R.id.sr_email_edittext);
        mobileEditText = (EditText)findViewById(R.id.sr_mobile_edittext);
        districtEditText = (EditText)findViewById(R.id.sr_district_edittext);
        zipCodeEditText = (EditText)findViewById(R.id.sr_zip_code_edittext);
        submitBtn = (Button)findViewById(R.id.sr_submit_btn);
    }

    private void customProgressBar(){
        LayoutInflater factory = LayoutInflater.from(ShopRegistration.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog01, null);
        dialog = new AlertDialog.Builder(ShopRegistration.this).create();
        dialog.setView(dialogView);
    }

    private void activateProgress(boolean flag){
        if(flag == true){
            dialog.show();
        }else{
            dialog.cancel();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.sr_state_textview:
                handleStateDialog();
                break;
            case R.id.sr_country_textview:
                handleCountryDialog();
                break;
            case R.id.sr_submit_btn:
                addDataToFireStore();
                break;

                //states ....
            case R.id.state_mp:
                stateTextView.setText("Madhya Pradesh");
                stateDialogState.cancel();
                break;
            case R.id.state_maharashtra:
                stateTextView.setText("Maharashtra");
                stateDialogState.cancel();
                break;
            case R.id.state_karnataka:
                stateTextView.setText("Karnataka");
                stateDialogState.cancel();
                break;
            case R.id.state_tamil_nadu:
                stateTextView.setText("Tamil Nadu");
                stateDialogState.cancel();
                break;

                //countries ....
            case R.id.country_india:
                countryTextView.setText("India");
                stateDialogCountry.cancel();
                break;
        }
    }

    private void addDataToFireStore() {

        String shopName = shopNameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String district = districtEditText.getText().toString();
        String state = stateTextView.getText().toString();
        String zipCode = zipCodeEditText.getText().toString();
        String country = countryTextView.getText().toString();

        if(!validateData(shopName, address, email, mobile, district, state, zipCode, country)){
            Toast.makeText(this, "Please fill all the input fields ...", Toast.LENGTH_SHORT).show();
            return;
        }

        ShopRegistrationData data = new ShopRegistrationData(
                shopName,
                address,
                email,
                mobile,
                district,
                state,
                zipCode,
                country
        );

        add(data);

    }

    private void add(ShopRegistrationData data) {

        activateProgress(true);
        CollectionReference collection = FirebaseFirestore.getInstance().collection("ShopRegistrationData");

        Map<String, ShopRegistrationData> map = new HashMap<>();
        map.put(SHOP_REGISTRATION_KEY, data);

        DocumentReference documentReference;

        if(USER_NAME.equals("NO_USER")){
            documentReference = collection.document();
        }else {
            documentReference = collection.document(USER_NAME);
        }

        documentReference.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activateProgress(false);
                        Toast.makeText(ShopRegistration.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ShopDashboard.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activateProgress(false);
                        Toast.makeText(ShopRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateData(String shopName, String address, String email, String mobile, String district, String state, String zipCode, String country) {

        if(
                shopName.matches("")
                || address.matches("")
                || email.matches("")
                || mobile.matches("")
                || district.matches("")
                || state.matches("")
                || zipCode.matches("")
                || country.matches("")
        ){
            return false;
        }
        return true;
    }

    private void handleCountryDialog() {
        LayoutInflater factory = LayoutInflater.from(ShopRegistration.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog03, null);

        RadioButton countryIndia = (RadioButton) dialogView.findViewById(R.id.country_india);
        countryIndia.setOnClickListener(this);

        stateDialogCountry = new AlertDialog.Builder(ShopRegistration.this).create();
        stateDialogCountry.setView(dialogView);

        stateDialogCountry.show();
    }

    private void handleStateDialog() {
        LayoutInflater factory = LayoutInflater.from(ShopRegistration.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog02, null);

        RadioButton stateMP = (RadioButton) dialogView.findViewById(R.id.state_mp);
        RadioButton stateMaharashtra = (RadioButton) dialogView.findViewById(R.id.state_maharashtra);
        RadioButton stateKarnataka = (RadioButton) dialogView.findViewById(R.id.state_karnataka);
        RadioButton stateTamilNadu = (RadioButton) dialogView.findViewById(R.id.state_tamil_nadu);
        stateMP.setOnClickListener(this);
        stateMaharashtra.setOnClickListener(this);
        stateKarnataka.setOnClickListener(this);
        stateTamilNadu.setOnClickListener(this);

        stateDialogState = new AlertDialog.Builder(ShopRegistration.this).create();
        stateDialogState.setView(dialogView);

        stateDialogState.show();
    }
}
