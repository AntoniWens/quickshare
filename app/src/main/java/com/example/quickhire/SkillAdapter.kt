package com.example.quickhire

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.quickhire.databinding.ItemSkillBinding
import com.example.quickhire.databinding.ItemVacancyBinding
import java.util.Locale

class SkillAdapter(private val list: ArrayList<String>) : RecyclerView.Adapter<SkillAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSkillBinding): RecyclerView.ViewHolder(binding.root)

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillAdapter.ViewHolder {

        val binding = ItemSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillAdapter.ViewHolder, position: Int) {

        val data = list[position]

        with(holder) {
            binding.textView19.text = data
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}