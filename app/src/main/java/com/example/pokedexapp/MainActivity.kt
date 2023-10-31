package com.example.pokedexapp

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedexapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val itemsList = mutableListOf<String>()

    private lateinit var adapter: AdapterRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configura el RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterRecyclerView(itemsList)
        recyclerView.adapter = adapter


        binding.svNombrePokemon.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.isNullOrEmpty()){
                    showError()
                }else{
                    buscarPokemonPorNombre(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/pokemon/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun buscarPokemonPorNombre(nombre: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getDataByCod(nombre)
            val datos = call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    //Imagen
                    val imageUrl = datos?.sprites?.other?.officialArtWork?.front_default ?: "Fallo al cargar la imagen"
                    Log.d("ImageURL", imageUrl) // Chivato
                    //Peso
                    val peso = datos?.weight?.let { pesoAKilos(it) }
                    //Altura
                    val altura = datos?.height?.let { alturaAMetros(it) }
                    //Tipos
                    itemsList.clear()
                    var tipoPokemonString = ""
                    datos?.types?.forEach { elemento->
                        tipoPokemonString += "${traducirTipoPokemon(elemento.type.name)}\n"
                    }
                    itemsList.add(tipoPokemonString)
                    adapter.notifyDataSetChanged()
                    //Estadisticas
                    var estadisticas = "Estadisticas:\n"
                    datos?.stats?.forEach { elemento->
                        estadisticas += "${elemento.stat.name}: ${elemento.base_stat}\n"
                    }


                    //jkjkjk
                    val pokemonInfo = peso?.let {
                        if (altura != null) {
                            PokemonInfo(
                                imageUrl = imageUrl,
                                nombre = nombre.capitalize(),
                                peso = it,
                                altura = altura,
                                tipos = tipoPokemonString,
                                estadisticas = estadisticas
                            )
                        } else {
                            null
                        }
                    }

                    val intent = Intent(this@MainActivity, MainActivity2::class.java)
                    intent.putExtra("pokemonInfo", pokemonInfo)
                    startActivity(intent)

                }else{
                    showError()
                }
            }
        }
    }
    private fun showError(){
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    fun alturaAMetros(altura: Int): Double = altura/10.0

    fun pesoAKilos(peso: Int): Double = peso/10.0

    fun traducirTipoPokemon(tipoIngles: String): String {
        return when (tipoIngles) {
            "normal" -> "Normal"
            "fire" -> "Fuego"
            "water" -> "Agua"
            "electric" -> "Eléctrico"
            "grass" -> "Planta"
            "ice" -> "Hielo"
            "fighting" -> "Lucha"
            "poison" -> "Veneno"
            "ground" -> "Tierra"
            "flying" -> "Volador"
            "psychic" -> "Psíquico"
            "bug" -> "Bicho"
            "rock" -> "Roca"
            "ghost" -> "Fantasma"
            "steel" -> "Acero"
            "dark" -> "Siniestro"
            "fairy" -> "Hada"
            else -> "Desconocido"
        }
    }
    fun obtenerColorFondoSegunTipo(tipoPokemon: String): Int {
        when (tipoPokemon) {
            "Normal" -> return R.color.colorNormal
            "Bicho" -> return R.color.colorBicho
            "Psíquico" -> return R.color.colorPsiquico
            "Tierra" -> return R.color.colorTierra
            "Veneno" -> return R.color.colorVeneno
            "Eléctrico" -> return R.color.colorElectrico
            "Planta" -> return R.color.colorPlanta
            "Hielo" -> return R.color.colorHielo
            "Lucha" -> return R.color.colorLucha
            "Volador" -> return R.color.colorVolador
            "Fantasma" -> return R.color.colorFantasma
            "Agua" -> return R.color.colorAgua
            "Fuego" -> return R.color.colorFuego
            "Siniestro" -> return R.color.colorSiniestro
            "Acero" -> return R.color.colorAcero
            else -> return R.color.colorNormal
        }
    }
}