package br.com.hellodev.controledevendas.data.model

import android.os.Parcelable
import br.com.hellodev.controledevendas.util.FirebaseHelper
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var establishment: String = "",
    var email: String = "",
    @get:Exclude
    var password: String = ""
) : Parcelable