package com.example.foodbits.api

import com.example.foodbits.models.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamApiService {
    @GET("search")
    fun getRecipes(
        @Query("q") query: String,
        @Query("app_id") appId: String="6cf6e756",
        @Query("app_key") appKey: String= "88bec5b3cdb41583b69d1eaae032d51b"
    ): Call<RecipeResponse>
}
