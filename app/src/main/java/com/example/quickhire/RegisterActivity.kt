package com.example.quickhire

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickhire.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val role = intent.getIntExtra("role", 0)

        if (role == 1) {
            binding.textView7.setText("EMPLOYEE")
        } else {
            binding.textView7.setText("JOB PROVIDER")
        }

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.registerBtn.setOnClickListener {
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            } else {

                val intent = Intent(this, NextRegisterActivity::class.java)
                intent.putExtra("role", role)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }
}