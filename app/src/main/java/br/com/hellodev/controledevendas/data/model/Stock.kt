package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import android.util.Log
import br.com.hellodev.controledevendas.util.FirebaseHelper
import com.google.firebase.database.ServerValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    val idProduct: String = "",
    val amount: Int = 0
) : Parcelable {

    fun save() {
        FirebaseHelper
            .getDatabase()
            .child("stock")
            .child(FirebaseHelper.userId())
            .child(idProduct)
            .setValue(this)
    }

    fun increment(amount: Int) {
        val updates: MutableMap<String, Any> = HashMap()
        updates["stock/${FirebaseHelper.userId()}/$idProduct/amount"] =
            ServerValue.increment(amount.toDouble())
        FirebaseHelper.getDatabase().updateChildren(updates)
    }

    fun decrement(amount: Int) {
        val updates: MutableMap<String, Any> = HashMap()
        updates["stock/${FirebaseHelper.userId()}/$idProduct/amount"] = ServerValue.increment(-amount.toDouble())
        FirebaseHelper.getDatabase().updateChildren(updates)
    }

}