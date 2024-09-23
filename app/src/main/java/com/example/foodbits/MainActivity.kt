package com.example.foodbits

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodbits.api.RetrofitClient
import com.example.foodbits.models.RecipeResponse
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val appId = "6e3459ca"
        val appKey = "3ba3e2604889681d691772a0b8dd818d"
        val query = "pollo" // Ejemplo de búsqueda

        if (auth.currentUser != null) {
            startActivity(Intent(this, Home::class.java))
        } else {
            startActivity(Intent(this, LogIn::class.java))
        }

        // No es necesario llamar a finish() aquí si deseas que MainActivity permanezca en la pila
        // finish()

        RetrofitClient.instance.getRecipes(query, appId, appKey).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.hits // Maneja la lista de recetas
                    // Actualiza tu UI con las recetas
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                // Manejo de errores
            }
        })
    }
}
