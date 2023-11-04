package com.example.pokedexapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedexapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: AdapterRecyclerView

    private lateinit var service: APIServiceList

    private val pokemonModelList = mutableListOf<PokemonModelRec>()

    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1 // Inicializa en la primera página
    private val PAGE_SIZE = 5 // Tamaño de página, puedes ajustarlo según tus necesidades


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configurar el RecyclerView
        setupRecyclerView()

        // Inicializar Retrofit y cargar los datos
        initRetrofitAndLoadData()

        // Configurar SearchView
        setupSearchView()

        binding.recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, binding.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    // Aquí obtienes el nombre del Pokémon en la posición seleccionada
                    val nombrePokemon = adapter.getPokemonNameAtPosition(position)
                    Log.d("NombrePokemonPulsar", nombrePokemon) // Chivato
                    //Lamo a cargar pokemon por nombre
                    buscarPokemonPorNombre(nombrePokemon)
                }
            })
        )


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
                    var tipoPokemonString = ""
                    datos?.types?.forEach { elemento->
                        tipoPokemonString += "${traducirTipoPokemon(elemento.type.name)}\n"
                    }
                    //Estadisticas
                    var estadisticas = "Estadisticas:\n"
                    datos?.stats?.forEach { elemento->
                        estadisticas += "${elemento.stat.name}: ${elemento.base_stat}\n"
                    }
                    //Lo paso a objeto para pasarlo a la segunda
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

    private fun loadPokemonData(offset: Int, limit: Int) {
        Log.e("TAG", "loadPokemonData")
        val call = service.getPokemonList(offset, limit)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val pokemonList = response.body()?.results
                    Log.e("TAG", "Respuesta de la API: $pokemonList")

                    var cont = 1
                    val deferredImages = pokemonList?.map { pokemon ->
                        Log.v("Pokemon", pokemon.name)
                        Log.v("numero",cont.toString())
                        cont++
                        async { buscarPokNombre(pokemon.name) }
                    }

                    val images = deferredImages?.map { it.await() }

                    images?.forEachIndexed { index, imageUrl ->
                        val pokemonModel = PokemonModelRec(pokemonList[index].name, imageUrl)
                        if (imageUrl.isNotEmpty()) {
                            pokemonModelList.add(pokemonModel)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Log.e("TAG", "Tamaño de pokemonModelList: ${pokemonModelList.size}")
                        adapter.updateData(pokemonModelList)
                    }
                } else {
                    // Maneja errores de respuesta
                    Log.e("API Error", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                // Maneja errores de conexión
                Log.e("API Error", "Error en la conexión: ${e.message}")
            }
        }
    }




    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterRecyclerView(this)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition + visibleItemCount >= totalItemCount && !isLoading) {
                    loadMoreData()
                }
            }
        })
    }


    private fun initRetrofitAndLoadData() {
        val client = OkHttpClient.Builder().apply {
            readTimeout(600, TimeUnit.SECONDS)
            writeTimeout(600, TimeUnit.SECONDS)
            connectTimeout(100, TimeUnit.SECONDS)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(APIServiceList::class.java)

        // Realiza la solicitud de datos
        loadPokemonData(offset = 0, limit = 10) // 1292 pokemons
        Log.e("TAG", "initRetrofitAndLoadData")
    }


    private fun setupSearchView() {


        binding.svNombrePokemon.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    showError()
                } else {
                    buscarPokemonPorNombre(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private suspend fun buscarPokNombre(nombre: String): String {
        val call = getRetrofit().create(APIService::class.java).getDataByCod(nombre)
        val datos = call.body()
        var imageUrl = ""

        if (call.isSuccessful) {
            // Imagen
            imageUrl = datos?.sprites?.other?.officialArtWork?.front_default ?: ""
        }

        return imageUrl
    }

    private fun loadMoreData() {
        if (!isLoading && !isLastPage) {
            isLoading = true
            // Actualiza el RecyclerView o carga más datos aquí
            currentPage++ // Aumenta la página
            loadPokemonData(offset = (currentPage - 1) * PAGE_SIZE, limit = PAGE_SIZE)
            isLoading = false
        }
    }


}