package com.example.chatapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.fragments.ChatFragment
import com.example.chatapp.fragments.LoginFragment
import com.example.chatapp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val bottomNavView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavView.requestFocus()
        bottomNavView.setOnItemSelectedListener { menuItem->
            Log.d("setonlistener","this is the log message")
            if (menuItem.itemId == R.id.chats_item) {
                Log.d("if m reached","this is the log message")
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame_layout, ChatFragment())
                    .commit()
            }

            if (menuItem.itemId == R.id.profile_item) {
                Log.d("if p reached","this is the log message")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame_layout, ProfileFragment()).commit()
            }
            return@setOnItemSelectedListener true
        }
        //binding.bottomNav.selectedItemId = R.id.chats_item

    }
}