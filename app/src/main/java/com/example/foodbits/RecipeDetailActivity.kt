package com.example.foodbits

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepsAdapter: StepsAdapter
    private val ingredientsList = mutableListOf<String>()
    private val stepsList = mutableListOf<String>() // Asegúrate de usar la clase Step
    private lateinit var recipeName: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var recipeImage: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val recipeId = intent.getStringExtra("RECIPE_ID") ?: return
        if (recipeId != null) {
            loadRecipeDetails(recipeId)  // Llamas a la función para cargar los detalles
        } else {
            Log.e("RecipeDetailActivity", "El ID de la receta es nulo")
        }

        // Configura el RecyclerView para ingredientes
        val ingredientsRecyclerView = findViewById<RecyclerView>(R.id.ingredients_recycler_view)
        ingredientsAdapter = IngredientsAdapter(ingredientsList)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsRecyclerView.adapter = ingredientsAdapter

        // Configura el RecyclerView para pasos
        val stepsRecyclerView = findViewById<RecyclerView>(R.id.steps_recycler_view)
        stepsAdapter = StepsAdapter(stepsList)
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = stepsAdapter

        recipeName = findViewById(R.id.recipe_name)
        recipeDescription = findViewById(R.id.recipe_description)
        recipeImage = findViewById(R.id.recipe_image)
    }

    private fun loadRecipeDetails(recipeId: String) {
        db.collection("recipes").document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val recipe = document.toObject(Recipe::class.java)
                    // Aquí puedes mostrar los detalles de la receta en tu UI
                    recipe?.let {
                        updateUI(it)
                    }
                } else {
                    Log.d("RecipeDetailActivity", "No se encontró la receta")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("RecipeDetailActivity", "Error al cargar detalles de la receta", exception)
            }
    }

    private fun updateUI(recipe: Recipe) {
        // Actualizar los datos de la UI
        recipeName.text = recipe.name
        recipeDescription.text = recipe.description

        // Cargar la imagen de la receta
        Glide.with(this).load(recipe.imageUrl).into(recipeImage)

        // Actualizar la lista de ingredientes
        ingredientsList.clear()
        ingredientsList.addAll(recipe.ingredients)
        ingredientsAdapter.notifyDataSetChanged()

        // Actualizar la lista de pasos
        stepsList.clear()
        stepsList.addAll(recipe.steps)
        stepsAdapter.notifyDataSetChanged()
    }
}
