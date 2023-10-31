package com.example.pokedexapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.pokedexapp.databinding.ActivityMain2Binding
import com.example.pokedexapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val pokemonInfo = intent.getParcelableExtra<PokemonInfo>("pokemonInfo")

        if (pokemonInfo != null) {
            // Uso de pokemon que me he traido desde m1
            val imageUrl = pokemonInfo.imageUrl
            val nombre = pokemonInfo.nombre
            val peso = pokemonInfo.peso
            val altura = pokemonInfo.altura
            val tipos = pokemonInfo.tipos
            val estadisticas = pokemonInfo.estadisticas


            //Imagen
            Picasso.get().load(imageUrl).into(binding.ivPokemon)
            //Nombre
            binding.tvNombrePokemon.text = nombre
            //Peso
            binding.cvPeso.visibility = View.VISIBLE
            binding.tvPeso.text = "Peso: $peso kg"
            //Altura
            binding.cvAltura.visibility = View.VISIBLE
            binding.tvAltura.text = "Altura: $altura m"
            //Tipos
            binding.cvTipos.visibility = View.VISIBLE
            binding.tvTipos.text = tipos
            //Estadistica
            binding.cvEstadisticas.visibility = View.VISIBLE
            binding.tvEstadisticas.text = estadisticas


            binding.ivBack.setOnClickListener {
                val intent = Intent(this@MainActivity2, MainActivity::class.java)
                startActivity(intent)
            }
        } else {

        }
    }
}