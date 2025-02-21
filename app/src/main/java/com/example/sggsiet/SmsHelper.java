package com.example.sggsiet;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class SmsHelper {

    private FirebaseAuth auth;
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    public SmsHelper() {
        auth = FirebaseAuth.getInstance();
    }

    public void sendOtp(Activity activity, String phoneNumber) {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("OTP_SUCCESS", "OTP Auto-verified");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("OTP_ERROR", "OTP verification failed: " + e.getMessage());
                Toast.makeText(activity, "OTP sending failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                SmsHelper.this.verificationId = verificationId;
                Log.d("OTP_SENT", "OTP sent successfully");
                Toast.makeText(activity, "OTP sent successfully", Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public String getVerificationId() {
        return verificationId;
    }
}
