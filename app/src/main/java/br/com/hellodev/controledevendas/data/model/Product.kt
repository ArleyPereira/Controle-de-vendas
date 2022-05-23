package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val amount: Int = 0,
    val sold: Int = 0,
    val costPrice: Float = 0f,
    val salePrice: Float = 0f
): Parcelable