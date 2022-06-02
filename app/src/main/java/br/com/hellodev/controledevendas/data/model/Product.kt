package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.showBottomSheet
import br.com.hellodev.controledevendas.util.snackBar
import com.google.firebase.ktx.Firebase
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
            .child(FirebaseHelper.getIdUser())
            .child(this.id)
            .setValue(this)
    }

}