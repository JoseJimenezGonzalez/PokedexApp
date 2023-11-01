package com.example.pokedexapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedexapp.databinding.TextRowItemBinding
import com.squareup.picasso.Picasso

class AdapterRecyclerView(private val context: Context) : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    private val dataSet: MutableList<PokemonModelRec> = mutableListOf()

    inner class ViewHolder(private val binding: TextRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemonModel: PokemonModelRec) {
            binding.textView.text = pokemonModel.nom.capitalize()
            Picasso.get().load(pokemonModel.urlCompleta).into(binding.ivFotoPokemon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TextRowItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val pokemonModel = dataSet[position]
        viewHolder.bind(pokemonModel)
    }

    fun updateData(newData: MutableList<PokemonModelRec>) {
        Log.d("AdapterRecyclerView", "${newData.size}")
        dataSet.clear()
        dataSet.addAll(newData)
        Log.d("AdapterRecyclerView", "${newData.size}")
        Log.d("AdapterRecyclerView", "Data set: ${dataSet.size}")
        notifyDataSetChanged()
    }
}

