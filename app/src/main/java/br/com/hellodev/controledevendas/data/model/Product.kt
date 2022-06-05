package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    var name: String = "",
    var costPrice: Float = 0f,
    var salePrice: Float = 0f
) : Parcelable {

    init {
        this.id = FirebaseHelper.getDatabase().push().key.toString()
    }

    fun save() {
        FirebaseHelper
            .getDatabase()
            .child("products")
            .child(FirebaseHelper.userId())
            .child(this.id)
            .setValue(this)
    }

}