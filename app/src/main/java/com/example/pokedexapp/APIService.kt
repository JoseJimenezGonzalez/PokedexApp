package com.example.pokedexapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getDataByCod(@Url url: String):Response<Pokemon>
}