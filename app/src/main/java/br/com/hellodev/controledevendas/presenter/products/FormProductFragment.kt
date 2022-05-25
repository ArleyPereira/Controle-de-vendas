package br.com.hellodev.controledevendas.presenter.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Product
import br.com.hellodev.controledevendas.databinding.FragmentFormProductBinding
import br.com.hellodev.controledevendas.util.*
import java.util.*

class FormProductFragment : BaseFragment() {

    private var _binding: FragmentFormProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var product: Product
    private var newProduct: Boolean = true

    private val args: FormProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.product.let {
            if (it != null) {
                this.product = it

                newProduct = false

                configData()
            }
        }
    }

    private fun configData() {
        binding.editName.setText(product.name)
        binding.editCostPrice.setText((product.costPrice * 10).toString())
        binding.editSalePrice.setText((product.salePrice * 10).toString())
    }

    private fun initListeners() {
        binding.editCostPrice.locale = Locale("pt", "BR")
        binding.editSalePrice.locale = Locale("pt", "BR")

        binding.btnSave.setOnClickListener { validadeData() }
    }

    private fun validadeData() {
        val name = binding.editName.text.toString().trim()
        val cost: Double = (binding.editCostPrice.rawValue.toDouble()) / 100
        val sale: Double = (binding.editSalePrice.rawValue.toDouble()) / 100

        if (name.isNotEmpty()) {
            if (cost > 0f) {
                if (sale > 0f) {

                    hideKeyboard()

                    binding.progressBar.isVisible = true

                    if (newProduct) {
                        product = Product(
                            name = name,
                            amount = 0,
                            sold = 0,
                            costPrice = cost,
                            salePrice = sale
                        )
                    } else {
                        product.name = name
                        product.costPrice = cost
                        product.salePrice = sale
                    }

                    saveProduct()

                } else {
                    showBottomSheet(message = "Informe o preço de venda  do produto.")
                }
            } else {
                showBottomSheet(message = "Informe o preço de custo do produto.")
            }
        } else {
            showBottomSheet(message = "Informe o nome do produto.")
        }
    }

    private fun saveProduct() {
        FirebaseHelper.getDatabase()
            .child("products")
            .child(FirebaseHelper.getIdUser())
            .child(product.id)
            .setValue(product)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    snackBar(R.string.text_save_sucess_form_product_fragment)

                    parentFragmentManager.setFragmentResult(
                        "KEY",
                        bundleOf(Pair("KEY", product))
                    )
                    findNavController().popBackStack()
                }
            }.addOnFailureListener { exception ->
                showBottomSheet(message = exception.message.toString())
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}