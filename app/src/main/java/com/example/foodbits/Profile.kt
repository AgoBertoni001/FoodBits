package com.example.foodbits

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var recipesList = ArrayList<Pair<String, Recipe>>()  // Cambiar la lista a usar Pair<String, Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializar RecyclerView desde el layout
        recyclerView = findViewById(R.id.recipes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pasar la función onClick al RecipeAdapter
        recipeAdapter = RecipeAdapter(recipesList) { recipeId, recipe ->
            // Intent para abrir los detalles de la receta seleccionada
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipeId)  // Pasar el ID de la receta
            intent.putExtra("RECIPE", recipe)  // Pasar la receta completa si es necesario
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter

        // Cargar recetas del usuario
        loadUserRecipes()
    }

    private fun loadUserRecipes() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("recipes")
            .whereEqualTo("userId", currentUser)
            .get()
            .addOnSuccessListener { result ->
                recipesList.clear()  // Limpiar la lista antes de agregar nuevas recetas
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    val recipeId = document.id  // Obtener el ID del documento
                    recipesList.add(Pair(recipeId, recipe))  // Agregar el Pair<String, Recipe> a la lista
                }
                recipeAdapter.notifyDataSetChanged()  // Notificar al adaptador que los datos cambiaron
            }
            .addOnFailureListener {
                // Manejar errores
            }
    }
}
