package com.example.foodbits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private var recipesList: List<Pair<String, Recipe>>,  // Lista de pares (ID, Recipe)
    private val onClick: (String, Recipe) -> Unit  // Función de clic que recibe ID y Recipe
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    // ViewHolder para el RecyclerView
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.findViewById(R.id.recipe_name)
        val recipeImage: ImageView = view.findViewById(R.id.recipe_image)
    }

    // Crea nuevas vistas (invocado por el LayoutManager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    // Reemplaza el contenido de una vista (invocado por el LayoutManager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Obtener el ID y la receta en la posición actual
        val (recipeId, recipe) = recipesList[position]
        Log.d("RecipeAdapter", "Mostrando receta: ${recipe.name}")

        // Asignar el nombre de la receta
        holder.recipeName.text = recipe.name

        // Si existe una URL de imagen válida, la mostramos, de lo contrario, ocultamos el ImageView
        if (recipe.imageUrl.isNotEmpty()) {
            holder.recipeImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(recipe.imageUrl)
                .into(holder.recipeImage)
        } else {
            holder.recipeImage.visibility = View.GONE
        }

        // Manejar el clic en la receta
        holder.itemView.setOnClickListener {
            Log.d("RecipeAdapter", "Clic en: ${recipe.name} con ID: $recipeId")
            onClick(recipeId, recipe)  // Pasa el ID y el objeto Recipe al manejador de clics
        }
    }

    // Devuelve el tamaño de la lista de recetas
    override fun getItemCount(): Int {
        return recipesList.size
    }

    // Función para actualizar los datos del adaptador (cuando cambian las recetas)
    fun updateData(newRecipes: List<Pair<String, Recipe>>) {
        Log.d("RecipeAdapter", "Actualizando datos con ${newRecipes.size} recetas")
        recipesList = newRecipes
        notifyDataSetChanged()  // Notifica al adaptador que los datos han cambiado
    }
}
