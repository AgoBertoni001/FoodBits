package com.example.foodbits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val recipesList: List<Pair<String, Recipe>>, private val onClick: (String, Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.findViewById(R.id.recipe_name)
        val recipeImage: ImageView = view.findViewById(R.id.recipe_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (recipeId, recipe) = recipesList[position]
        holder.recipeName.text = recipe.name

        // Si existe una imagen, la cargamos; si no, ocultamos el ImageView
        if (recipe.imageUrl.isNotEmpty()) {
            holder.recipeImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(recipe.imageUrl)
                .into(holder.recipeImage)
        } else {
            holder.recipeImage.visibility = View.GONE
        }

        // Llamamos al listener para manejar el clic en una receta, pasando el recipeId
        holder.itemView.setOnClickListener {
            Log.d("RecipeAdapter", "Clic en: ${recipe.name} con ID: $recipeId")
            onClick(recipeId, recipe)  // Pasa el id y el objeto Recipe
        }
    }


    override fun getItemCount(): Int {
        return recipesList.size
    }
}

