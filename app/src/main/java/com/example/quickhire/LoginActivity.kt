package com.example.quickhire

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickhire.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val progress = ProgressCustom(this)

        val role = intent.getIntExtra("role", 0)

        firebaseAuth = Firebase.auth
        database = Firebase.database

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterJobProvider::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }

        val preferences = Preferences(this)

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            } else {
                progress.show()
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    database.reference.child("users").child(it.user?.uid ?: "").get().addOnSuccessListener { db ->
                        progress.dismiss()
                        if (db.exists()) {
                            val data = db.getValue(User::class.java)
                            if (data?.role == 1) {
                                val intent = Intent(this, BottomNavigationActivity2::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, BottomNavigationActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }

                            if (data != null) {
                                preferences.save(data)
                            }
                        }
                    }

                }.addOnFailureListener {
                    progress.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}