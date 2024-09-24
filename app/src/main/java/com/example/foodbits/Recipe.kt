package com.example.foodbits

import java.io.Serializable

enum class Visibility {
    PUBLIC,
    PRIVATE
}

data class Recipe (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val imageUrl: String = "",
    val visibility: Visibility = Visibility.PUBLIC,
    val userId: String = ""
): Serializable

// Mapea las recetas de la API al formato local de tu app
fun mapApiRecipeToLocalRecipe(apiRecipe: com.example.foodbits.models.Recipe): Recipe {
    return Recipe(
        id = apiRecipe.uri,  // Usa el 'uri' como ID único
        name = apiRecipe.label,  // 'label' es el nombre de la receta en la API
        imageUrl = apiRecipe.image,  // 'image' es la URL de la imagen de la receta
        ingredients = apiRecipe.ingredients.map { it.text }  // Mapea los ingredientes de la API a una lista de strings
    )
}






