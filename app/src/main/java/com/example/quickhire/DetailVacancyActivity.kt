package com.example.quickhire

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhire.databinding.ActivityDetailVacancyBinding
import com.example.quickhire.databinding.AppliedSuccessDialogBinding
import com.example.quickhire.databinding.CloseSuccessDialogBinding
import com.example.quickhire.databinding.CloseVacancyDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailVacancyActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailVacancyBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailVacancyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("job", Job::class.java)
        } else {
            intent.getSerializableExtra("job") as Job
        }

        val preferences = Preferences(this)
        val progress = ProgressCustom(this)

        firebaseAuth = Firebase.auth
        database = Firebase.database

        if (data != null) {
            binding.companyTxt.text = data.companyName
            binding.salaryTxt.text = data.salary
            binding.locationEdt.text = data.location
            binding.descTxt.text = data.jobDesc
            binding.workTxt.text = data.jobTitle
            binding.skillRv.layoutManager = LinearLayoutManager(this)

            binding.skillRv.adapter = SkillAdapter(data.skill as ArrayList<String>)
        }

        if (preferences.getUser().role == 1) {
            binding.bookmarkImg.isVisible = true
            binding.applicants.isVisible = false
            binding.closeVacancyBtn.text = "Apply Job"
        } else {
            binding.bookmarkImg.isVisible = false
            binding.applicants.isVisible = true
            binding.closeVacancyBtn.isVisible = data?.status == 1
        }

        binding.backImg.setOnClickListener {
            finish()
        }

        database.reference.child("bookmark/${firebaseAuth.currentUser?.uid}/${data?.jobId}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    binding.bookmarkImg.setImageResource(R.drawable.active_bookmark)
                } else {
                    binding.bookmarkImg.setImageResource(R.drawable.bookmark)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.applicantsBtn.setOnClickListener {
            if(preferences.getUser().role == 1) {
                database.reference.child("bookmark/${firebaseAuth.currentUser?.uid}/${data?.jobId}").get().addOnSuccessListener {
                    if(!it.exists()){
                        database.reference.child("bookmark/${firebaseAuth.currentUser?.uid}/${data?.jobId}").setValue(data).addOnSuccessListener {

                        }
                    } else {
                        database.reference.child("bookmark/${firebaseAuth.currentUser?.uid}/${data?.jobId}").removeValue()
                    }
                }.addOnFailureListener {
                }
            } else {
                val intent = Intent(this, ApplicantActivity::class.java)
                intent.putExtra("id", data?.jobId)
                startActivity(intent)
            }
        }

        database.reference.child("users/${firebaseAuth.currentUser?.uid}/jobs/${data?.jobId}").addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.closeVacancyBtn.isVisible = !snapshot.exists()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.closeVacancyBtn.setOnClickListener {
            if(preferences.getUser().role == 2) {
                val dialog = AlertDialog.Builder(this)
                val binding = CloseVacancyDialogBinding.inflate(LayoutInflater.from(this))
                dialog.setView(binding.root)
                dialog.setCancelable(false)
                val d = dialog.show()
                binding.okBtn.setOnClickListener {
                    d.dismiss()
                    progress.show()
                    database.reference.child("jobs/${data?.jobId}").updateChildren(
                        mapOf("status" to 0)
                    ).addOnSuccessListener {
                        progress.dismiss()
                        showScDialog()
                    }.addOnFailureListener {
                        progress.dismiss()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                binding.disagreeBtn.setOnClickListener {
                    d.dismiss()
                }


            } else {
                val id = System.currentTimeMillis()
                val datas = Applicants(id,"Applied for ${data?.jobTitle} (${data?.companyName})", preferences.getUser().fullName)
                progress.show()

                database.reference.child("applicants/${data?.uid}/$id").setValue(datas).addOnSuccessListener {
                    database.reference.child("jobs/${data?.jobId}/applied/$id").setValue(datas)
                    database.reference.child("users/${firebaseAuth.currentUser?.uid}/jobs/${data?.jobId}").setValue(data)
                    progress.dismiss()
                    showScAppliedDialog()
                }.addOnFailureListener {
                    progress.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showScDialog() {
        val dialog = AlertDialog.Builder(this)
        val binding = CloseSuccessDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setView(binding.root)
        dialog.setCancelable(false)

        val d = dialog.show()

        binding.okBtn.setOnClickListener {
            d.dismiss()
            finish()
        }
    }

    private fun showScAppliedDialog() {
        val dialog = AlertDialog.Builder(this)
        val binding = AppliedSuccessDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setView(binding.root)
        dialog.setCancelable(false)

        val d = dialog.show()

        binding.okBtn.setOnClickListener {
            d.dismiss()
            finish()
        }


    }
}