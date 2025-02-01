package com.example.quickhire

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.databinding.ActivityApplicantBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ApplicantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicantBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApplicantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = Firebase.database

        val id = intent.getLongExtra("id", 0)
        val list = ArrayList<Applicants>()

        Log.d("2504", id.toString())

        binding.progressBar5.isVisible = true

        binding.imageView5.setOnClickListener {
            finish()
        }

        binding.appRv.layoutManager = LinearLayoutManager(this)
        database.reference.child("jobs/${id}/applied").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar5.isVisible = false
                list.clear()
                snapshot.children.forEach { s ->
                    Log.d("2504", "cc")
                    val data = s.getValue(Applicants::class.java)
                    if (data != null) {
                        list.add(data)
                    }
                }

                binding.appRv.adapter = ApplicantAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}