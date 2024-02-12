package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.window.SplashScreen
import com.example.chatapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

         Handler().postDelayed({
             val intent = Intent(this@SplashActivity, VerificationActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
             startActivity(intent)
         }, 3000)
    }
}