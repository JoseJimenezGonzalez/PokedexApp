package com.example.pokedexapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedexapp.databinding.TextRowItemBinding

class AdapterRecyclerView(private val context: Context) : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    private val dataSet: MutableList<String> = mutableListOf()

    inner class ViewHolder(private val binding: TextRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemonName: String) {
            binding.textView.text = pokemonName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TextRowItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val pokemonName = dataSet[position]
        viewHolder.bind(pokemonName)
    }

    fun updateData(newData: List<String>) {
        dataSet.clear()
        dataSet.addAll(newData)
        notifyDataSetChanged()
    }
}

