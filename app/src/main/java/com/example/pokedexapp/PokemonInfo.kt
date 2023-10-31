package com.example.pokedexapp

import android.os.Parcel
import android.os.Parcelable

data class PokemonInfo(
    val imageUrl: String,
    val nombre: String,
    val peso: Double,
    val altura: Double,
    val tipos: String, // Cambiado a String
    val estadisticas: String // Cambiado a String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "", // Cambiado a String
        parcel.readString() ?: "" // Cambiado a String
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(nombre)
        parcel.writeDouble(peso)
        parcel.writeDouble(altura)
        parcel.writeString(tipos) // Cambiado a String
        parcel.writeString(estadisticas) // Cambiado a String
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PokemonInfo> {
        override fun createFromParcel(parcel: Parcel): PokemonInfo {
            return PokemonInfo(parcel)
        }

        override fun newArray(size: Int): Array<PokemonInfo?> {
            return arrayOfNulls(size)
        }
    }
}

