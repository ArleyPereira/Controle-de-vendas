package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    var name: String = "",
    var amount: Int = 0,
    var sold: Int = 0,
    var costPrice: Double = 0.0,
    var salePrice: Double = 0.0
) : Parcelable {

    init {
        this.id = FirebaseHelper.getDatabase().push().key.toString()
    }

}