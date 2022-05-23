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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
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

fun Double.formatedValue(): String {
    val nf: NumberFormat = DecimalFormat(
        "#,##0.00", DecimalFormatSymbols(
            Locale("PT", "br")
        )
    )
    return nf.format(this)
}