package br.com.hellodev.controledevendas.util

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.text.*
import java.util.*

fun Fragment.initToolbar(toolbar: Toolbar, HomeAsUpEnabled: Boolean = true) {
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).title = ""
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(HomeAsUpEnabled)
    (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
}

fun Context.toast(resource: Int): Toast = Toast
    .makeText(this, resource, Toast.LENGTH_SHORT)
    .apply { show() }

fun Fragment.snackBar(resource: Int): Snackbar =
    Snackbar.make(this.requireView(), resource, Snackbar.LENGTH_SHORT)
        .apply { show() }

fun Fragment.showBottomSheet(
    titleDialog: Int? = null,
    titleButton: Int? = null,
    message: String,
    buttonCancel: Boolean? = false,
    onOkClick: () -> Unit = {}
) {
    val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    val binding: BottomSheetBinding =
        BottomSheetBinding.inflate(layoutInflater, null, false)

    binding.txtTitle.text = getText(titleDialog ?: R.string.text_title_warning)
    binding.txtMessage.text = message

    binding.btnOK.text = getText(titleButton ?: R.string.text_button_warning)
    binding.btnOK.setOnClickListener {
        onOkClick()
        bottomSheetDialog.dismiss()
    }

    binding.btnCancel.isVisible = buttonCancel ?: false
    binding.btnCancel.setOnClickListener {
        bottomSheetDialog.dismiss()
    }

    bottomSheetDialog.setContentView(binding.root)
    bottomSheetDialog.show()
}

fun Float.formatedValue(): String {
    val nf: NumberFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("PT", "br")))
    nf.currency = Currency.getInstance("BRL")
    return nf.format(this)
}

fun Long.formatDate(type: Int): String {
    val DIA_MES_ANO = 1 // 31/12/2021
    val HORA_MINUTO = 2 // 22:00
    val DIA_MES_ANO_HORA_MINUTO = 3 // 31/12/2021 ás 22:00
    val DIA_MES = 4 // 31 Janeiro

    val locale = Locale("PT", "br")

    val fuso = "America/Sao_Paulo"

    val diaSdf = SimpleDateFormat("dd", locale)
    diaSdf.timeZone = TimeZone.getTimeZone(fuso)

    val mesSdf = SimpleDateFormat("MM", locale)
    mesSdf.timeZone = TimeZone.getTimeZone(fuso)

    val anoSdf = SimpleDateFormat("yyyy", locale)
    anoSdf.timeZone = TimeZone.getTimeZone(fuso)

    val horaSdf = SimpleDateFormat("HH", locale)
    horaSdf.timeZone = TimeZone.getTimeZone(fuso)

    val minutoSdf = SimpleDateFormat("mm", locale)
    minutoSdf.timeZone = TimeZone.getTimeZone(fuso)

    val dateFormat = DateFormat.getDateInstance()
    val netDate = Date(this)
    dateFormat.format(netDate)

    val hora = horaSdf.format(netDate)
    val minuto = minutoSdf.format(netDate)

    val dia = diaSdf.format(netDate)
    var mes = mesSdf.format(netDate)
    val ano = anoSdf.format(netDate)

    if (type == 4) {
        mes = when (mes) {
            "01" -> "janeiro"
            "02" -> "fevereiro"
            "03" -> "março"
            "04" -> "abril"
            "05" -> "maio"
            "06" -> "junho"
            "07" -> "julho"
            "08" -> "agosto"
            "09" -> "setembro"
            "10" -> "outubro"
            "11" -> "novembro"
            "12" -> "dezembro"
            else -> ""
        }
    }
    val time: String = when (type) {
        DIA_MES_ANO -> "$dia/$mes/$ano"
        HORA_MINUTO -> "$hora:$minuto"
        DIA_MES_ANO_HORA_MINUTO -> "$dia/$mes/$ano ás $hora:$minuto"
        DIA_MES -> "$dia $mes"
        else -> "Erro"
    }
    return time
}