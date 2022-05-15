package com.example.controledevendas.presenter.products

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.controledevendas.R
import com.example.controledevendas.data.model.Product
import com.example.controledevendas.databinding.BottomSheetAddSaleProductBinding
import com.example.controledevendas.databinding.BottomSheetStockProductBinding
import com.example.controledevendas.databinding.FragmentProductsBinding
import com.example.controledevendas.presenter.adapter.ProductAdapter
import com.example.controledevendas.presenter.adapter.TypeSelected
import com.example.controledevendas.util.formatedPrice
import com.example.controledevendas.util.initToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog


class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar, false)

        initAdapter()
    }

    private fun initAdapter() {
        productAdapter = ProductAdapter(requireContext()) { product, selected ->
            when (selected) {
                TypeSelected.Option -> {

                }
                TypeSelected.Stock -> {
                    showAdjustmentStock(product)
                }
                else -> {
                    showAddSale(product)
                }
            }
        }

        with(binding.rvProducts) {
            setHasFixedSize(true)
            adapter = productAdapter
        }

        productAdapter.submitList(getProducts())
    }

    private fun showAddSale(product: Product) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding: BottomSheetAddSaleProductBinding =
            BottomSheetAddSaleProductBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.textProduct.text = product.name
        bottomSheetBinding.edtPrice.setText(product.salePrice.formatedPrice())

        bottomSheetBinding.ibMinus.setOnClickListener {
            if (bottomSheetBinding.edtSold.text.toString().isNotEmpty()) {
                val increment = bottomSheetBinding.edtSold.text.toString().toInt() - 1
                bottomSheetBinding.edtSold.setText(increment.toString())
            } else {
                bottomSheetBinding.edtSold.setText("1")
            }
        }

        bottomSheetBinding.btnSave.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.ibPlus.setOnClickListener {
            if (bottomSheetBinding.edtSold.text.toString().isNotEmpty()) {
                val increment = bottomSheetBinding.edtSold.text.toString().toInt() + 1

                bottomSheetBinding.edtSold.setText(increment.toString())
            } else {
                bottomSheetBinding.edtSold.setText("1")
            }
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun showAdjustmentStock(product: Product) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding: BottomSheetStockProductBinding =
            BottomSheetStockProductBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.textProduct.text = product.name
        bottomSheetBinding.edtPrice.setText(product.salePrice.formatedPrice())

        bottomSheetBinding.ibMinus.setOnClickListener {
            if (bottomSheetBinding.edtSold.text.toString().isNotEmpty()) {
                val increment = bottomSheetBinding.edtSold.text.toString().toInt() - 1
                bottomSheetBinding.edtSold.setText(increment.toString())

                // Altera o texto do botão caso de acordo com a quantidade negativa e positiva
                setStateButtonStock(bottomSheetBinding)
            } else {
                bottomSheetBinding.edtSold.setText("1")
            }
        }

        bottomSheetBinding.btnSave.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.ibPlus.setOnClickListener {
            if (bottomSheetBinding.edtSold.text.toString().isNotEmpty()) {
                val increment = bottomSheetBinding.edtSold.text.toString().toInt() + 1
                bottomSheetBinding.edtSold.setText(increment.toString())

                // Altera o texto do botão caso de acordo com a quantidade negativa e positiva
                setStateButtonStock(bottomSheetBinding)
            } else {
                bottomSheetBinding.edtSold.setText("1")
            }
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    /**
     * @author Arley Santana
     * Altera o texto do botão caso de acordo com a quantidade negativa e positiva
     */
    private fun setStateButtonStock(bottomSheetBinding: BottomSheetStockProductBinding) {
        if (bottomSheetBinding.edtSold.text.toString().toInt() >= 0) {
            bottomSheetBinding.btnSave.text = "Adicionar estoque"
        } else if (bottomSheetBinding.edtSold.text.toString().toInt() < 0) {
            bottomSheetBinding.btnSave.text = "Retirar estoque"
        }
    }

    private fun getProducts() = listOf(
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
    )

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_products, menu)
        val item = menu.findItem(R.id.menu_search)
        binding.searchView.setMenuItem(item)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}