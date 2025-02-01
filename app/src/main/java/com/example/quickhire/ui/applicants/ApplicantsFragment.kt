package com.example.quickhire.ui.applicants

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.ApplicantAdapter
import com.example.quickhire.Applicants
import com.example.quickhire.Preferences
import com.example.quickhire.ProgressCustom
import com.example.quickhire.User
import com.example.quickhire.databinding.FragmentApplicantsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ApplicantsFragment : Fragment() {

    private var _binding: FragmentApplicantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = Firebase.auth
        database = Firebase.database
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicantsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = Preferences(requireContext())
        val progress = ProgressCustom(requireContext())

        if(preferences.getUser().role == 1) {
            binding.employee.isVisible = true
            binding.nameEdt.setText(preferences.getUser().fullName)
            binding.businessEdt.setText(preferences.getUser().detailBusiness)
            binding.locationEdt.setText(preferences.getUser().location)
            binding.phoneEdt.setText(preferences.getUser().phone)
            binding.emailEdt.setText(preferences.getUser().email)

            binding.saveBtn.setOnClickListener {
                progress.show()
                database.reference.child("users/${firebaseAuth.currentUser?.uid}").updateChildren(
                    mapOf("fullName" to binding.nameEdt.text.toString(),
                        "detailBusiness" to binding.businessEdt.text.toString(),
                        "location" to binding.locationEdt.text.toString(),
                        "phone" to binding.phoneEdt.text.toString(),
                )).addOnSuccessListener {
                    progress.dismiss()
                    val user = User(
                        fullName = binding.nameEdt.text.toString(),
                        email = binding.emailEdt.text.toString(),
                        uid = firebaseAuth.currentUser?.uid ?: "",
                        detailBusiness = binding.businessEdt.text.toString(),
                        location = binding.locationEdt.text.toString(),
                        phone = binding.phoneEdt.text.toString(),
                        role = preferences.getUser().role
                    )

                    preferences.save(user)
                }.addOnFailureListener {
                    progress.dismiss()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.jobProvder.isVisible = true
            binding.applicantsRv.layoutManager = LinearLayoutManager(context)

            val list = ArrayList<Applicants>()

            database.reference.child("applicants/${firebaseAuth.currentUser?.uid}").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { s ->
                        val data = s.getValue(Applicants::class.java)
                        if (data != null) {
                            list.add(data)
                        }
                    }

                    binding.applicantsRv.adapter = ApplicantAdapter(list)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}