package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    var id: String = "",
    var idProduct: String = "",
    var amount: Double = 0.0, // Preço de venda - preço de custo
    var date: Long = 0L
) : Parcelable {

    init {
        this.id = FirebaseHelper.getDatabase().push().key.toString()
    }

}