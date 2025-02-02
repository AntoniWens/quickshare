package com.example.quickhire.ui.add_vacancy

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.Job
import com.example.quickhire.JobAdapter
import com.example.quickhire.Preferences
import com.example.quickhire.ProgressCustom
import com.example.quickhire.SkillAdapter
import com.example.quickhire.databinding.AddSkillDialogBinding
import com.example.quickhire.databinding.AddSuccessDialogBinding
import com.example.quickhire.databinding.CloseSuccessDialogBinding
import com.example.quickhire.databinding.FragmentAddVacancyBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddVacancyFragment : Fragment() {

    private var _binding: FragmentAddVacancyBinding? = null
    private val binding get() = _binding!!



    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    lateinit var skills: ArrayList<String>
    lateinit var adapter: SkillAdapter

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

        _binding = FragmentAddVacancyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = Preferences(requireContext())

        if(preferences.getUser().role == 1) {
            binding.employee.isVisible = true
            binding.jobProvider.isVisible = false

            val adapter = ViewPagerAdapter(this)
            binding.viewpager.adapter = adapter

            TabLayoutMediator(
                binding.tabLayout2, binding.viewpager
            ) { tab, position -> tab.text = if(position == 0) "Applied Job" else "Bookmark" }.attach()


        } else {
            binding.jobProvider.isVisible = true
            binding.employee.isVisible = false
            binding.skillAddRv.layoutManager = LinearLayoutManager(context)

            val progress = ProgressCustom(requireContext())

            skills = ArrayList()
            adapter = SkillAdapter(skills)

            binding.skillAddRv.adapter = adapter

            binding.addSkillBtn.setOnClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                val binding = AddSkillDialogBinding.inflate(LayoutInflater.from(context))
                dialog.setView(binding.root)
                dialog.setCancelable(false)
                val d = dialog.show()
                binding.okBtn.setOnClickListener {
                    val skill = binding.skillEdt.text.toString()
                    if (skill.isNotEmpty()) {
                        d.dismiss()
                        skills.add(skill)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Data kosong", Toast.LENGTH_SHORT).show()
                    }

                }

                binding.disagreeBtn.setOnClickListener {
                    d.dismiss()
                }


            }

            binding.addVacancyBtn.setOnClickListener {
                val jobTitle = binding.jobTitleEdt.text.toString()
                val company = binding.companyNameEdt.text.toString()
                val salary = binding.salaryEdt.text.toString()
                val location = binding.locationEdt.text.toString()
                val desc = binding.jobDescEdt.text.toString()
                val id = System.currentTimeMillis()


                if (jobTitle.isBlank() || company.isBlank() || salary.isBlank() || location.isBlank() || desc.isBlank() || skills.isEmpty()) {
                    Toast.makeText(context, "Data belum lengkap", Toast.LENGTH_SHORT).show()
                } else {
                    progress.show()
                    val data = Job(
                        jobId = id,
                        jobTitle,
                        company,
                        salary,
                        location,
                        desc,
                        uid = firebaseAuth.currentUser?.uid ?: "",
                        skill = skills,
                        status = 1
                    )

                    database.reference.child("jobs/${id}").setValue(data).addOnSuccessListener {
                        progress.dismiss()
                        showScDialog()
                    }.addOnFailureListener{
                        progress.dismiss()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }






    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showScDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val binding = AddSuccessDialogBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setView(binding.root)
        dialog.setCancelable(false)

        val d = dialog.show()

        binding.okBtn.setOnClickListener {
            d.dismiss()
            clearAdd()
            skills.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun clearAdd() {
        binding.jobTitleEdt.text?.clear()
        binding.companyNameEdt.text?.clear()
        binding.salaryEdt.text?.clear()
       binding.locationEdt.text?.clear()
        binding.jobDescEdt.text?.clear()
    }
}