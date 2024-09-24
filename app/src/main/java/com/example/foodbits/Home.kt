package com.example.foodbits

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodbits.api.RetrofitClient
import com.example.foodbits.models.RecipeResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipeAdapter
    private var recipesList = mutableListOf<Pair<String, Recipe>>()  // Lista combinada de recetas
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Toggle para abrir/cerrar el Drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Inicializa el BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> true
                R.id.action_create_recipe -> {
                    startActivity(Intent(this, CreateRecipe::class.java))
                    true
                }
                R.id.action_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
                else -> false
            }
        }

        // Para mostrar las recetas
        recipesRecyclerView = findViewById(R.id.recipes_recycler_view)
        recipesAdapter = RecipeAdapter(recipesList) { recipeId, recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipeId)  // Pasar el id de la receta seleccionada
            startActivity(intent)
        }
        recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        recipesRecyclerView.adapter = recipesAdapter

        // Cargar las recetas públicas desde Firebase
        loadPublicRecipes()

        // Cargar recetas desde la API de EDAMAM
        fetchRecipesFromApi("pollo")
    }

    // Función que llama a la API de EDAMAM para obtener recetas
    private fun fetchRecipesFromApi(query: String) {
        RetrofitClient.instance.getRecipes(query).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    Log.d("API Response", "Respuesta exitosa: ${response.body()}")
                    val apiRecipes = response.body()?.hits ?: emptyList()

                    // Mapeamos las recetas de la API a tu modelo local de recetas
                    val mappedRecipes = apiRecipes.map { hit ->
                        val recipe = mapApiRecipeToLocalRecipe(hit.recipe)
                        Pair(recipe.id, recipe)
                    }

                    // Añadir recetas de la API a la lista combinada
                    recipesList.addAll(mappedRecipes)
                    recipesAdapter.notifyDataSetChanged()
                } else {
                    Log.e("API Error", "Código de error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Log.e("API Failure", "Error: ${t.message}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> { /* No es necesario reiniciar Home */ }
            R.id.action_create_recipe -> {
                startActivity(Intent(this, CreateRecipe::class.java))
            }
            R.id.action_profile -> {
                startActivity(Intent(this, Profile::class.java))
            }
            else -> return false
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Carga las recetas públicas desde Firebase y combina con las de la API
    private fun loadPublicRecipes() {
        db.collection("recipes")
            .whereEqualTo("visibility", "PUBLIC")  // Filtra solo recetas públicas
            .get()
            .addOnSuccessListener { result ->
                recipesList.clear()  // Limpiar la lista antes de añadir nuevas recetas
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    val recipeId = document.id  // Obtiene el ID del documento
                    recipesList.add(Pair(recipeId, recipe))  // Añadir receta de Firebase
                }
                recipesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Error al cargar recetas", exception)
            }
    }

    // Función para actualizar las recetas en el RecyclerView
    private fun RecipeAdapter.updateRecipes(recipes: List<Pair<String, Recipe>>) {
        recipesList.clear()
        recipesList.addAll(recipes)
        notifyDataSetChanged()
    }
}
