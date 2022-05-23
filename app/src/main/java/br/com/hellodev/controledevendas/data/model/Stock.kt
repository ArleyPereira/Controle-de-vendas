package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    val id: String = "", // idProduct
    val amount: Int = 0
) : Parcelable