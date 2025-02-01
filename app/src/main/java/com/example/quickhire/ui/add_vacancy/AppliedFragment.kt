package com.example.quickhire.ui.add_vacancy

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.Job
import com.example.quickhire.JobAdapter
import com.example.quickhire.R
import com.example.quickhire.databinding.FragmentAppliedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AppliedFragment : Fragment() {

    private lateinit var binding: FragmentAppliedBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var context: Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context =context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database
        firebaseAuth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentAppliedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appliedRv.layoutManager = LinearLayoutManager(context)

        val list = ArrayList<Job>()
        val skills = ArrayList<String>()
        database.reference.child("users/${firebaseAuth.currentUser?.uid}/jobs")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    binding.progressBar3.isVisible = false
                    list.clear()
                    snapshot.children.forEach { e ->
                        Log.d("2504", "ccc1")
                        skills.clear()
                        if (snapshot.exists()) {
                            e.child("skill").children.forEach { s ->
                                skills.add(s.value.toString())
                            }
                        }
                        list.add(
                            Job(
                            jobId = e.child("jobId").value.toString().toLong(),
                            jobTitle = e.child("jobTitle").value.toString(),
                            companyName = e.child("companyName").value.toString(),
                            salary = e.child("salary").value.toString(),
                            location = e.child("location").value.toString(),
                            jobDesc = e.child("jobDesc").value.toString(),
                            skill = skills
                        )
                        )
                    }
                    val adapter = JobAdapter(list)
                    binding.appliedRv.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}