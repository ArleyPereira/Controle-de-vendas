package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper
import com.google.firebase.database.ServerValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sale(
    var id: String = "",
    var idProduct: String = "",
    var currentPrice: Double = 0.0,
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
            .child(FirebaseHelper.getIdUser())
            .child(this.idProduct)
            .child(this.id)
            .setValue(this)
    }

}