package com.example.foodbits.models

data class RecipeResponse(
    val hits: List<Hit>
)

data class Hit(
    val recipe: Recipe
)

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val text: String,
    val quantity: Double,
    val measure: String
)
