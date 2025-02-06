package com.example.quickhire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickhire.databinding.ActivityNextRegisterBinding

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class NextRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNextRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNextRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val progress = ProgressCustom(this)

        firebaseAuth = Firebase.auth
        database = Firebase.database

        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val role = intent.getIntExtra("role", 0)

        if(role == 1) {
            binding.textView92.setText("Pilihan Bidang")
        }

        if (role == 1) {
            binding.textView7.setText("EMPLOYEE")
        } else {
            binding.textView7.setText("JOB PROVIDER")
        }

        val preferences = Preferences(this)
        binding.backImg.setOnClickListener {
            finish()
        }

        binding.registerNextBtn.setOnClickListener{
            val name = binding.nameEdt.text.toString()
            val detailBusiness = binding.businessEdt.text.toString()
            val location = binding.locationEdt.text.toString()
            val phoneNumber = binding.phoneEdt.text.toString()

            if (name.isBlank() || detailBusiness.isBlank() || location.isBlank() || phoneNumber.isBlank()) {
                Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("2504", "run")
                progress.show()
                firebaseAuth.createUserWithEmailAndPassword(email!!, password!!).addOnSuccessListener {

                    val data = User(
                        email,
                        it.user!!.uid,
                        name,
                        detailBusiness,
                        location,
                        phoneNumber,
                        role
                    )

                    database.reference.child("users/${it.user!!.uid}").setValue(data).addOnSuccessListener {
                        progress.dismiss()
                        if (data.role == 1) {
                            val intent = Intent(this, BottomNavigationActivity2::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, BottomNavigationActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        preferences.save(data)
                    }.addOnFailureListener { e->
                        progress.dismiss()
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    progress.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}