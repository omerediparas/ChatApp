package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.chatapp.databinding.ActivityVerificationBinding
import com.google.firebase.FirebaseApp

/*
    button -> (pressed) load the phone number to the otp and initiate the otpActivity
 */
class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Call this method before any Firebase APIs in components outside the main process.
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.isVisible = false
        val ccp = binding.countryCodePicker
        ccp.ccpDialogShowNameCode = false

        binding.button.setOnClickListener {
           // gets the ccp
            val phoneNumberEditText : EditText = binding.editTextPhoneNumber
            ccp.registerCarrierNumberEditText(phoneNumberEditText)

            if(!ccp.isValidFullNumber){
                Toast.makeText(this,"Please give a valid telephone number", Toast.LENGTH_LONG).show()
            }
            else{
                binding.progressBar.isVisible = true
                binding.button.apply {
                    isVisible = false
                    isActivated = false
                    Thread.sleep(1000)
                }
                val phoneNumber = ccp.formattedFullNumber
                val intent = Intent(this@VerificationActivity, OTPActivity::class.java)
                intent.putExtra("phoneNumber", phoneNumber)
                startActivity(intent)
            }
        }
    }
}

