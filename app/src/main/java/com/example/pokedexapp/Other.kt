package com.example.pokedexapp

import com.google.gson.annotations.SerializedName

data class Other(
    @SerializedName("official-artwork")
    val officialArtWork: OfficialArtWork
)
