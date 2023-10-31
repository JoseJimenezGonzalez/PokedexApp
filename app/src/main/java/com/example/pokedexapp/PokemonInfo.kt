package com.example.pokedexapp

import android.os.Parcel
import android.os.Parcelable

data class PokemonInfo(
    val imageUrl: String,
    val nombre: String,
    val peso: Double,
    val altura: Double,
    val tipos: String,
    val estadisticas: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(nombre)
        parcel.writeDouble(peso)
        parcel.writeDouble(altura)
        parcel.writeString(tipos)
        parcel.writeString(estadisticas)
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

