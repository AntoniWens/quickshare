package com.example.quickhire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickhire.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        database = Firebase.database

        binding.playBtn.setOnClickListener {

            if (auth.currentUser == null) {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
            } else {
                database.reference.child("users/${auth.currentUser?.uid}").get().addOnSuccessListener {
                    val data  = it.getValue(User::class.java)

                    if (data?.role == 1) {
                        val intent = Intent(this, BottomNavigationActivity2::class.java)
                        intent.putExtra("role", data.role)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, BottomNavigationActivity::class.java)
                        intent.putExtra("role", data?.role)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}