package com.example.pokedexapp

data class PokemonList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: MutableList<PokemonListData>
)
