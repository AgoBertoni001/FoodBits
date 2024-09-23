package com.example.foodbits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StepsAdapter(private var steps: MutableList<String>) :
    RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepText: TextView = itemView.findViewById(R.id.step_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.stepText.text = steps[position]
    }

    override fun getItemCount(): Int {
        return steps.size
    }

    // Método para agregar un paso a la lista
    fun addStep(step: String) {
        steps.add(step)  // Esto ahora funcionará
        notifyItemInserted(steps.size - 1)
    }

    fun getStepsList(): List<String> {
        return steps
    }
}
