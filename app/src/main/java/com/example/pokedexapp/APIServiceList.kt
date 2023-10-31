package com.example.pokedexapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServiceList {
    @GET("pokemon")
    fun getPokemonList(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<PokemonList>
}