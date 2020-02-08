package com.orderprio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ShopRegistration extends AppCompatActivity implements View.OnClickListener {

    private TextView stateTextView, countryTextView;
    private EditText shopNameEditText, addressEditText, emailEditText, mobileEditText, districtEditText, zipCodeEditText;
    private Button submitBtn;
    private AlertDialog stateDialogState, stateDialogCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_registration);

        initViews();

        //click events ...
        stateTextView.setOnClickListener(this);
        countryTextView.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.sr_state_textview:
                handleStateDialog();
                break;
            case R.id.sr_country_textview:
                handleCountryDialog();
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
