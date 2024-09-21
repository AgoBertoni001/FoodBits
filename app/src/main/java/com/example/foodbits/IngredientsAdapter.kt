package com.example.foodbits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsAdapter(private var ingredients: MutableList<String>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientText: TextView = itemView.findViewById(R.id.ingredient_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.ingredientText.text = ingredients[position]
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    // Método para agregar un ingrediente a la lista
    fun addIngredient(ingredient: String) {
        ingredients.add(ingredient)
        notifyItemInserted(ingredients.size - 1)
    }

    fun getIngredientsList(): List<String> {
        return ingredients
    }
}

