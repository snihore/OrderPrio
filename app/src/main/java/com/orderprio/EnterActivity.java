package com.orderprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class EnterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText id, password;
    private Button signupButton, loginButton;
    private RadioButton emailRadioBtn, mobileRadioBtn;
    private FirebaseAuth mAuth;
    private String mobileVerificationID;
    private boolean isEmail;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        customProgressBar();

        activateProgress(false);

        //email radio btn is pressed ...
        emailRadioBtn.setChecked(true);
        isEmail = true;

        //click events
        signupButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        emailRadioBtn.setOnClickListener(this);
        mobileRadioBtn.setOnClickListener(this);


    }

    private void goToOptionActivity(){
        Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
        startActivity(intent);
    }

    private void customProgressBar(){
        LayoutInflater factory = LayoutInflater.from(EnterActivity.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog01, null);
        dialog = new AlertDialog.Builder(EnterActivity.this).create();
        dialog.setView(dialogView);
    }

    private void activateProgress(boolean flag){
        if(flag == true){
            dialog.show();
        }else{
            dialog.cancel();
        }
    }
    private void initViews() {

        id = (EditText)findViewById(R.id.enter_id);
        password = (EditText)findViewById(R.id.enter_password);
        signupButton = (Button)findViewById(R.id.signup_button);
        loginButton = (Button)findViewById(R.id.login_button);
        emailRadioBtn = (RadioButton)findViewById(R.id.email_radio_btn);
        mobileRadioBtn = (RadioButton)findViewById(R.id.mobile_radio_btn);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.signup_button:
                if(isEmail){
                    validateCredentials("Email", "Signup");
                }else{
                    validateCredentials("Mobile", "GETOTP");
                }
                break;
            case R.id.login_button:
                if(isEmail){
                    validateCredentials("Email", "Login");
                }else{
                    validateCredentials("Mobile", "VARIFYOTP");
                }
                break;
            case R.id.email_radio_btn:
                signupButton.setText("Signup");
                loginButton.setText("Login");
                isEmail = true;
                break;
            case R.id.mobile_radio_btn:
                signupButton.setText("Get OTP");
                loginButton.setText("Verify OTP");
                isEmail = false;
                break;
        }
    }


    private void validateCredentials(String flag01, String flag02) {
        String idStr = id.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if(idStr.matches("") || idStr.matches("")){
            Toast.makeText(this, "Please fill all the input fields ...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(flag01.equals("Email")){
            //Email Auth ...

            if(!idStr.contains("@gmail.com")){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                return;
            }

            checkPassword(idStr, passwordStr, flag02);
        }else if(flag01.equals("Mobile")){
            //Mobile Auth ...

            try{
                Long.parseLong(idStr);
                if(idStr.length() != 10){
                    Toast.makeText(this, "Invalid Mobile", Toast.LENGTH_SHORT).show();
                    return;
                }
                mobileSignup(idStr, passwordStr, flag02);

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Invalid Mobile", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void checkPassword(String idStr, String passwordStr, String flag) {
        
        if(passwordStr.length()<6){
            Toast.makeText(this, "Password length should be 6 or more ...", Toast.LENGTH_SHORT).show();
        }else{
            
            enter(idStr, passwordStr, flag);
        }
    }

    private void enter(String idStr, String passwordStr, String flag) {

        if(flag.equals("Signup")){
            activateProgress(true);
            emailSignup(idStr, passwordStr);
        }else if(flag.equals("Login")){
            activateProgress(true);
            emailLogin(idStr, passwordStr);
        }
    }

    private void emailLogin(final String idStr, String passwordStr) {
        mAuth.signInWithEmailAndPassword(idStr, passwordStr)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(idStr.equals(authResult.getUser().getEmail())){
                            activateProgress(false);
                            Toast.makeText(EnterActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            goToOptionActivity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activateProgress(false);
                        Toast.makeText(EnterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }

    private void emailSignup(final String idStr, String passwordStr) {
        mAuth.createUserWithEmailAndPassword(idStr, passwordStr)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(idStr.equals(authResult.getUser().getEmail())){
                    activateProgress(false);
                    Toast.makeText(EnterActivity.this, "Signup Success", Toast.LENGTH_SHORT).show();
                    goToOptionActivity();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activateProgress(false);
                Toast.makeText(EnterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void mobileSignup(String idStr, String passwordStr, String flag) {

        activateProgress(true);
        if(flag.equals("GETOTP")){
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91"+idStr,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }else if(flag.equals("VARIFYOTP")){
            if(password.getText().toString().matches("")){
                Toast.makeText(this, "Please enter verification code ...", Toast.LENGTH_SHORT).show();
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mobileVerificationID, password.getText().toString().trim());
            signInWithPhoneAuthCredential(credential);
        }

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            Log.d(TAG, "onVerificationCompleted:" + credential);

            //auto fill
            password.setText(credential.getSmsCode());

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
            activateProgress(false);
            Toast.makeText(EnterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
//            Log.d(TAG, "onCodeSent:" + verificationId);

            activateProgress(false);
            mobileVerificationID = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        activateProgress(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                            activateProgress(false);
                            Toast.makeText(EnterActivity.this, "Mobile Success", Toast.LENGTH_SHORT).show();
                            goToOptionActivity();
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            activateProgress(false);
                            Toast.makeText(EnterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
