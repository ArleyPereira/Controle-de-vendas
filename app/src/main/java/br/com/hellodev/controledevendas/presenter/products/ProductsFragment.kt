package br.com.hellodev.controledevendas.presenter.products

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.databinding.BottomSheetAddSaleProductBinding
import br.com.hellodev.controledevendas.databinding.BottomSheetStockProductBinding
import br.com.hellodev.controledevendas.databinding.FragmentProductsBinding
import br.com.hellodev.controledevendas.data.model.Product
import br.com.hellodev.controledevendas.presenter.adapter.ProductAdapter
import br.com.hellodev.controledevendas.presenter.adapter.TypeSelected
import br.com.hellodev.controledevendas.util.formatedPrice
import br.com.hellodev.controledevendas.util.initToolbar
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProductsFragment : Fragment() {

    private val TAG = "INFOTESTE"

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

        initListeners()

        binding.textInfo.text = ""
        binding.progressBar.isVisible = false
    }

    /**
     * @author Arley Santana
     * Ouvinte de todos os elementos da view
     */
    private fun initListeners() {
        binding.simpleSearchView.setOnQueryTextListener(object :
            SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText.isNotEmpty()) {
                    val productList = getProducts().filter { it.name.contains(newText, true) }

                    productAdapter.submitList(productList)

                    binding.textInfo.text = if (productList.isNotEmpty()) {
                        ""
                    } else {
                        "Nenhum produto encontrado."
                    }
                    true
                } else {
                    productAdapter.submitList(getProducts())

                    binding.textInfo.text = if (getProducts().isNotEmpty()) {
                        ""
                    } else {
                        "Nenhum produto encontrado."
                    }

                    setPositionRecyclerView()
                    false
                }
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        binding.simpleSearchView.setOnSearchViewListener(object :
            SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                productAdapter.submitList(getProducts())
                binding.textInfo.text = if (getProducts().isNotEmpty()) {
                    ""
                } else {
                    "Nenhum produto encontrado."
                }
            }

            override fun onSearchViewClosedAnimation() {

            }

            override fun onSearchViewShown() {

            }

            override fun onSearchViewShownAnimation() {

            }

        })
    }

    private fun setPositionRecyclerView() {
        productAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.rvProducts.scrollToPosition(0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            }
        })
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

                setStateModalSale(bottomSheetBinding)
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

                setStateModalSale(bottomSheetBinding)
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
     * Altera o texto do botão de acordo com a quantidade negativa e positiva do estoque
     */
    private fun setStateButtonStock(bottomSheetBinding: BottomSheetStockProductBinding) {
        if (bottomSheetBinding.edtSold.text.toString().toInt() >= 0) {
            bottomSheetBinding.btnSave.text = "Adicionar estoque"
        } else if (bottomSheetBinding.edtSold.text.toString().toInt() < 0) {
            bottomSheetBinding.btnSave.text = "Retirar estoque"
        }
    }

    /**
     * @author Arley Santana
     * Altera o título da modal de acordo com a quantidade negativa e positiva do estorno
     */
    private fun setStateModalSale(bottomSheetBinding: BottomSheetAddSaleProductBinding) {
        if (bottomSheetBinding.edtSold.text.toString().toInt() >= 0) {
            bottomSheetBinding.textTitle.text = "Adicionar venda"
            bottomSheetBinding.btnSave.text = "Adicionar"
        } else if (bottomSheetBinding.edtSold.text.toString().toInt() < 0) {
            bottomSheetBinding.textTitle.text = "Estornar venda"
            bottomSheetBinding.btnSave.text = "Estornar"
        }
    }

    private fun getProducts() = listOf(
        Product(
            "",
            "Refrigerador Electrolux DFN41 Frost Free com Painel de Controle Externo 371L - Branco",
            10,
            5,
            80.0f,
            120.0f
        ),
        Product(
            "",
            "Cooktop a Gás Philco 5 Bocas Chef 5 Bisote Bivolt – Preto",
            10,
            5,
            80.0f,
            120.0f
        ),
        Product("", "Forno Micro-ondas Philco PMO34 Espelhado - 34 L", 10, 5, 80.0f, 120.0f),
        Product(
            "",
            "Liquidificador Osterizer Clássico Edição Limitada 75 Anos",
            10,
            5,
            80.0f,
            120.0f
        ),
        Product(
            "",
            "Fogão Industrial 4 Bocas 30x30 Perfil 7 Em Aço Inox com Forno Tampa de Vidro",
            10,
            5,
            80.0f,
            120.0f
        ),
        Product(
            "",
            "Sofá 3 Lugares Luizzi Monet Retrátil e Reclinável em Veludo 191cm de Largura",
            10,
            5,
            80.0f,
            120.0f
        ),
        Product("", "Cadeira Executiva", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52S 5G Branco", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy S21", 10, 5, 80.0f, 120.0f),
        Product("", "Smartphone Samsung Galaxy A52s 5G 128GB 6.5 6GB Branco", 10, 5, 80.0f, 120.0f),
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                findNavController().navigate(R.id.action_menu_products_to_formProductFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_products, menu)
        val item = menu.findItem(R.id.menu_search)
        binding.simpleSearchView.setMenuItem(item)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}