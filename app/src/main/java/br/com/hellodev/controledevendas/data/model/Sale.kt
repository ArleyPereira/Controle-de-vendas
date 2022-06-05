package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sale(
    var id: String = "",
    var idProduct: String = "",
    var currentPrice: Float = 0f,
    var amount: Int = 0,
    var date: Long = 0L
) : Parcelable {

    init {
        this.id = FirebaseHelper.getDatabase().push().key.toString()
    }

    fun save(){
        FirebaseHelper
            .getDatabase()
            .child("sales")
            .child(FirebaseHelper.userId())
            .child(this.id)
            .setValue(this)
    }

}