package com.example.quickhire.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.Job
import com.example.quickhire.JobAdapter
import com.example.quickhire.MainActivity
import com.example.quickhire.Preferences
import com.example.quickhire.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var context: Context

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    var adapter: JobAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
        database = Firebase.database
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = Preferences(context)

        val skills = ArrayList<String>()
        val list = ArrayList<Job>()

        binding.fullNameTxt.text = preferences.getUser().fullName
        binding.roleTxt.text = if (preferences.getUser().role == 1) "Employee" else "Job Provider"

        binding.jobnRv.layoutManager = LinearLayoutManager(context)
        binding.logout.setOnClickListener {
            firebaseAuth.signOut()
            preferences.clear()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        binding.progressBar2.isVisible = true
        if (preferences.getUser().role == 2) {
            database.reference.child("jobs")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.progressBar2.isVisible = false
                        list.clear()
                        snapshot.children.forEach { e ->
                            if (e.child("uid").value.toString() == firebaseAuth.currentUser?.uid && e.child("status").value.toString() == "1") {
                                skills.clear()
                                if (snapshot.exists()) {
                                    e.child("skill").children.forEach { s ->
                                        Log.d("2504", s.value.toString())
                                        skills.add(s.value.toString())
                                    }
                                }
                                list.add(
                                    Job(
                                        uid = e.child("uid").value.toString(),
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
                            adapter = JobAdapter(list)
                            binding.jobnRv.adapter = adapter
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } else {
            database.reference.child("jobs")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.progressBar2.isVisible = false
                        list.clear()
                        snapshot.children.forEach { e ->
                            if (e.child("status").value.toString() == "1") {
                                skills.clear()
                                if (snapshot.exists()) {
                                    e.child("skill").children.forEach { s ->
                                        skills.add(s.value.toString())
                                    }
                                }
                                if (e.child("jobId").exists()) {
                                    list.add(
                                        Job(
                                            uid = e.child("uid").value.toString(),
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

                            }
                            adapter = JobAdapter(list)
                            binding.jobnRv.adapter = adapter
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        binding.searchCard.isVisible = preferences.getUser().role == 1

        binding.searchEdt.doOnTextChanged { text, start, before, count ->
            adapter?.filter?.filter(text)
        }

    }


}