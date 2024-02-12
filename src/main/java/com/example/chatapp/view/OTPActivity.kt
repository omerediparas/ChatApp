package com.example.chatapp.view

import android.content.Intent
import android.graphics.Typeface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class OTPActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOtpBinding
    val auth = FirebaseAuth.getInstance()
    var phoneNumber: String? = null
    private lateinit var forceResendingToken: ForceResendingToken
    private lateinit var authId: String
    var timeRemaining = 30 // TODO NOT 30 SECONDS BUT 60.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phoneNumber")
        binding.verifyNumberText.text = "Verify ${phoneNumber}"
        // what if user is already exists.
        sendOtp(false)


        binding.button.setOnClickListener {
            val enteredCode = binding.otpView.text.toString()
            val credential = PhoneAuthProvider.getCredential(authId,enteredCode)
            signIn(credential)
        }
        binding.resendText.setOnClickListener {
            sendOtp(true)
        }
    }

    fun sendOtp(isResend: Boolean) {
        val options: PhoneAuthOptions.Builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    signIn(p0)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(
                        this@OTPActivity,
                        p0.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                    // why it cant see toast class???
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    authId = p0
                    forceResendingToken = p1
                    startTimer()
                }
            })
        // android needs to know if the verification is of resend or first time.
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(
                options.setForceResendingToken(forceResendingToken).build()
            )
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options.build())
        }

    }


    private fun signIn(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this, SetupActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(
                        this@OTPActivity,
                        "verification failed",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    private fun startTimer() {
        // countdown needs to be done

        Timer().scheduleAtFixedRate(object : TimerTask(){
            override  fun run() {
                timeRemaining--
                if(timeRemaining <= 1){
                    runOnUiThread {
                        binding.resendText.text = "Resend id"
                        binding.resendText.typeface = Typeface.DEFAULT_BOLD
                    }
                    this.cancel()
                }
                else{
                    runOnUiThread {
                        binding.resendText.text = "Resend id in ${timeRemaining} seconds "
                    }
                }
            }
        },0,1000)

    }

}