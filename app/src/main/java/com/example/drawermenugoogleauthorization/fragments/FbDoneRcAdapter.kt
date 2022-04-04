package com.example.drawermenugoogleauthorization.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.task.Task

class FbDoneRcAdapter(val tasks: ArrayList<Task>, val listener: OnItemClickListener): RecyclerView.Adapter<FbDoneRcAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_done_rcview_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTitle.text = task.title
        holder.tvDesc.text = task.description
        holder.tvTime.text = "${task.hour} : ${task.minute}"
        holder.tvDate.text = "${task.dayOfMonth}/${task.month}/${task.year}"
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var tvTitle: TextView
        var tvDesc: TextView
        var tvTime : TextView
        var tvDate : TextView
        var btnDelete: Button
        var btnActivate : Button

        init{
            tvTitle = itemView.findViewById(R.id.taskTitle)
            tvDesc = itemView.findViewById(R.id.taskDescription)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvDate = itemView.findViewById(R.id.tvDate)
            btnDelete = itemView.findViewById(R.id.btnDeleteTask)
            btnActivate = itemView.findViewById(R.id.btnActivateTask)

            tvDesc.setOnClickListener(this)
            tvTitle.setOnClickListener(this)
            btnDelete.setOnClickListener(this)
            btnActivate.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                if(p0?.id == tvTitle.id || p0?.id == tvDesc.id){
                    listener.onItemClick(position)
                }
                if (p0?.id == btnDelete.id)
                    listener.onBtnDeleteClick(position)
                else if(p0?.id == btnActivate.id)
                    listener.onBtnActivateClicked(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
        fun onBtnDeleteClick(position: Int)
        fun onBtnActivateClicked(position: Int)
    }

}