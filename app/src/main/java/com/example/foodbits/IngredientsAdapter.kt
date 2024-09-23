package com.example.foodbits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsAdapter(private val ingredientsList: MutableList<String>) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientTextView: TextView = view.findViewById(R.id.ingredient_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredientsList[position]
        holder.ingredientTextView.text = ingredient
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    // Método para agregar un ingrediente
    fun addIngredient(ingredient: String) {
        ingredientsList.add(ingredient)
        notifyItemInserted(ingredientsList.size - 1)
    }

    // Método para obtener la lista mutable
    fun getIngredientsList(): MutableList<String> {
        return ingredientsList
    }
}
