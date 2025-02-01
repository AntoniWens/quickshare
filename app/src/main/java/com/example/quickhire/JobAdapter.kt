package com.example.quickhire

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.quickhire.databinding.ItemVacancyBinding
import java.util.Locale

class JobAdapter(list: ArrayList<Job>) : RecyclerView.Adapter<JobAdapter.ViewHolder>(), Filterable {

    inner class ViewHolder(val binding: ItemVacancyBinding): RecyclerView.ViewHolder(binding.root)

    lateinit var context: Context

    var listM: ArrayList<Job> = list
    var listMFull: ArrayList<Job> = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobAdapter.ViewHolder {

        val binding = ItemVacancyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobAdapter.ViewHolder, position: Int) {

        val data = listM[position]

        with(holder) {
            binding.jobTitleTxt.text = data.jobTitle
            binding.salaryTxt.text = data.salary
            binding.companyNameTxt.text = data.companyName
            binding.locationTxt.text = data.location

            itemView.setOnClickListener {
                val intent = Intent(context, DetailVacancyActivity::class.java)
                intent.putExtra("job", data)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return listM.size
    }

    override fun getFilter(): Filter {
        return filter()
    }

    private fun filter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredListToneModel = ArrayList<Job>()
            if (constraint.isNullOrEmpty()) {
                filteredListToneModel.addAll(listMFull)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.ROOT)
                for (item: Job in listMFull) {
                    if (item.jobTitle.lowercase(Locale.ROOT).contains(filterPattern)) {
                        filteredListToneModel.add(item)
                    } else if (item.companyName.lowercase(Locale.ROOT).contains(filterPattern)) {
                        filteredListToneModel.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredListToneModel
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            listM.clear()
            listM.addAll(results?.values as ArrayList<Job>)
            notifyDataSetChanged()
        }

    }
}