package com.example.controledevendas.util

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

fun Fragment.initToolbar(toolbar: Toolbar, HomeAsUpEnabled: Boolean = true){
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).title = ""
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(HomeAsUpEnabled)
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
}

fun Float.formatedPrice() : String {
    val nf: NumberFormat = DecimalFormat(
        "#,##0.00",
        DecimalFormatSymbols(Locale("pt", "BR"))
    )
    return nf.format(this)
}