package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper

@kotlinx.parcelize.Parcelize
data class Expense(
    var id: String = "",
    var description: String = "",
    var amount: Float = 0f,
    var date: Long = 0L
): Parcelable {

    init {
        this.id = FirebaseHelper.getDatabase().push().key.toString()
    }

    fun save(){
        FirebaseHelper
            .getDatabase()
            .child("expenses")
            .child(FirebaseHelper.getIdUser())
            .child(this.id)
            .setValue(this)
    }

}