package com.nareshkumarrao.eiweblog.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nareshkumarrao.eiweblog.ExamRow
import com.nareshkumarrao.eiweblog.R

class ItemGradesAdapter(private val examRows: List<ExamRow>) : RecyclerView.Adapter<ItemGradesAdapter.ViewHolder>() {
    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_grades, parent, false)) {
        private var name: TextView? = null
        private var attempt: TextView? = null
        private var date: TextView? = null
        private var grade: TextView? = null

        init {
            name = itemView.findViewById(R.id.examNameText)
            attempt = itemView.findViewById(R.id.versuchText)
            date = itemView.findViewById(R.id.examDateText)
            grade = itemView.findViewById(R.id.gradeText)
        }

        fun bind(examRow: ExamRow) {
            name?.text = examRow.name
            attempt?.text = examRow.attempt
            grade?.text = examRow.grade
            date?.text = examRow.date
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGradesAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val examRow = examRows[position]
        holder.bind(examRow)
    }

    override fun getItemCount(): Int {
        return examRows.size
    }
}