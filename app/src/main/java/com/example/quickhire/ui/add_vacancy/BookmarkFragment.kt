package com.example.quickhire.ui.add_vacancy

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.Job
import com.example.quickhire.JobAdapter
import com.example.quickhire.R
import com.example.quickhire.databinding.FragmentBookmarkBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var context: Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
        database = Firebase.database
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookmarkRv.layoutManager = LinearLayoutManager(context)

        val skills = ArrayList<String>()
        val list = ArrayList<Job>()

        database.reference.child("bookmark/${firebaseAuth.currentUser?.uid}").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar4.isVisible = false
                list.clear()
                snapshot.children.forEach { e ->
                    skills.clear()
                    if (snapshot.exists()) {
                        e.child("skills").children.forEach { s ->
                            skills.add(s.child("skill").value.toString())
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
                binding.bookmarkRv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}