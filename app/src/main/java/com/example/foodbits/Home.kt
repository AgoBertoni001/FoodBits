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
import com.example.foodbits.CreateRecipe
import com.example.foodbits.RecipeDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipeAdapter
    private var recipesList = mutableListOf<Pair<String, Recipe>>()
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
                R.id.action_home -> {
                    // Navegar a Home
                    true
                }
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


        recipesRecyclerView = findViewById(R.id.recipes_recycler_view)
        recipesAdapter = RecipeAdapter(recipesList) { recipeId, recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipeId)  // Pasar el id de la receta seleccionada
            startActivity(intent)
        }


        recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        recipesRecyclerView.adapter = recipesAdapter

        loadPublicRecipes()

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
            R.id.action_home -> {
                // No es necesario reiniciar Home
            }
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

    private fun loadPublicRecipes() {
        db.collection("recipes")
            .whereEqualTo("visibility", "PUBLIC")  // Filtra solo recetas públicas
            .get()
            .addOnSuccessListener { result ->
                recipesList.clear()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    val recipeId = document.id  // Obtiene el ID del documento
                    recipesList.add(Pair(recipeId, recipe)) // Guarda el ID y la receta como un par
                }
                recipesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Error al cargar recetas", exception)
            }
    }

}
