package com.example.quickhire

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.quickhire.databinding.ItemApplicantBinding
import com.example.quickhire.databinding.ItemSkillBinding
import com.example.quickhire.databinding.ItemVacancyBinding
import java.util.Locale

class ApplicantAdapter(private val list: ArrayList<Applicants>) : RecyclerView.Adapter<ApplicantAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemApplicantBinding): RecyclerView.ViewHolder(binding.root)

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantAdapter.ViewHolder {

        val binding = ItemApplicantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicantAdapter.ViewHolder, position: Int) {

        val data = list[position]

        with(holder) {
            binding.name.text = data.userName
            binding.desc.text = data.desc
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}