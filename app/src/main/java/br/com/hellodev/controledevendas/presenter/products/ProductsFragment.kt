package br.com.hellodev.controledevendas.presenter.products

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Product
import br.com.hellodev.controledevendas.data.model.Stock
import br.com.hellodev.controledevendas.databinding.BottomSheetAddSaleProductBinding
import br.com.hellodev.controledevendas.databinding.BottomSheetMoreProductBinding
import br.com.hellodev.controledevendas.databinding.BottomSheetStockProductBinding
import br.com.hellodev.controledevendas.databinding.FragmentProductsBinding
import br.com.hellodev.controledevendas.presenter.adapter.ProductAdapter
import br.com.hellodev.controledevendas.presenter.adapter.TypeSelected
import br.com.hellodev.controledevendas.util.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProductsFragment : BaseFragment() {

    private val TAG = "INFOTESTE"

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

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

        getProducts()

        listenerFragment()
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
                    val newList = productList.filter { it.name.contains(newText, true) }

                    productAdapter.submitList(newList)

                    listEmpty(productAdapter.currentList)
                    true
                } else {
                    productAdapter.submitList(productList)

                    listEmpty(productAdapter.currentList)

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
                productAdapter.submitList(productList)

                listEmpty(productAdapter.currentList)
            }

            override fun onSearchViewClosedAnimation() {

            }

            override fun onSearchViewShown() {

            }

            override fun onSearchViewShownAnimation() {

            }

        })
    }

    private fun getProducts() {
        FirebaseHelper.getDatabase()
            .child("products")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productList.clear()
                        for (ds in snapshot.children) {
                            productList.add(ds.getValue(Product::class.java) as Product)
                        }

                        val productList = productList
                        productList.reverse()

                        productAdapter.submitList(productList)
                        productAdapter.notifyDataSetChanged()
                    }

                    listEmpty(productAdapter.currentList)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun deleteProduct(product: Product) {
        FirebaseHelper.getDatabase()
            .child("products")
            .child(FirebaseHelper.getIdUser())
            .child(product.id)
            .removeValue()
            .addOnCompleteListener {
                // Armazena a lista atual do adapter
                val oldList = productAdapter.currentList

                // Gera uma nova lista a partir da lista antiga já com a tarefa atualizada
                val newList = oldList.toMutableList().apply {
                    remove(product)
                }

                productAdapter.submitList(newList)

                // Deleta o produto da lista local
                productList.remove(product)

                listEmpty(newList)

                snackBar(R.string.text_message_delete_sucess_products_fragment)
            }.addOnFailureListener {
                showBottomSheet(message = getString(R.string.error_generic))
            }
    }

    private fun listEmpty(productList: List<Product>) {
        binding.textInfo.text = if (productList.isNotEmpty()) {
            ""
        } else {
            "Nenhum produto encontrado."
        }

        binding.progressBar.isVisible = false
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

            hideKeyboard()

            when (selected) {
                TypeSelected.Option -> {
                    showMoreOptionProduct(product)
                }
                TypeSelected.Stock -> {
                    showAddStock(product)
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
    }

    private fun showMoreOptionProduct(product: Product) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding: BottomSheetMoreProductBinding =
            BottomSheetMoreProductBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.textProduct.text = product.name

        bottomSheetBinding.btnRemove.setOnClickListener {
            bottomSheetDialog.dismiss()

            showBottomSheet(
                titleDialog = R.string.text_title_dialog_delete_products_fragment,
                message = getString(R.string.text_message_dialog_delete_products_fragment),
                titleButton = R.string.text_button_confirm_delete_products_fragment,
                buttonCancel = true,
                onOkClick = { deleteProduct(product) }
            )
        }

        bottomSheetBinding.btnEdit.setOnClickListener {
            bottomSheetDialog.dismiss()

            val action = ProductsFragmentDirections
                .actionMenuProductsToFormProductFragment(product)
            findNavController().navigate(action)
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun showAddSale(product: Product) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding: BottomSheetAddSaleProductBinding =
            BottomSheetAddSaleProductBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.textProduct.text = product.name
        bottomSheetBinding.edtPrice.setText(product.salePrice.formatedValue())

        var amount = 1
        bottomSheetBinding.ibMinus.setOnClickListener {
            amount = if (amount > 1) {
                bottomSheetBinding.edtSold.text.toString().toInt() - 1
            } else {
                1
            }

            bottomSheetBinding.edtSold.setText(amount.toString())
        }

        bottomSheetBinding.ibPlus.setOnClickListener {
            if (bottomSheetBinding.edtSold.text.toString().isNotEmpty()) {
                amount = bottomSheetBinding.edtSold.text.toString().toInt() + 1
                bottomSheetBinding.edtSold.setText(amount.toString())
            } else {
                bottomSheetBinding.edtSold.setText(amount.toString())
            }
        }

        bottomSheetBinding.btnSave.setOnClickListener {
            product.salePrice = bottomSheetBinding.edtPrice.rawValue.toDouble() / 100
            updateStock(product, amount)

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun showAddStock(product: Product) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding: BottomSheetStockProductBinding =
            BottomSheetStockProductBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.textProduct.text = product.name
        bottomSheetBinding.edtPrice.setText(product.costPrice.formatedValue())

        var amount = 1
        bottomSheetBinding.ibMinus.setOnClickListener {
            if (bottomSheetBinding.edtAmount.text.toString().isNotEmpty()) {
                amount = bottomSheetBinding.edtAmount.text.toString().toInt() - 1

                if (amount > 0) {
                    bottomSheetBinding.edtAmount.setText(amount.toString())
                } else {
                    amount = 1
                }

            } else {
                bottomSheetBinding.edtAmount.setText(amount.toString())
            }
        }

        bottomSheetBinding.ibPlus.setOnClickListener {
            if (bottomSheetBinding.edtAmount.text.toString().isNotEmpty()) {
                amount = bottomSheetBinding.edtAmount.text.toString().toInt() + 1

                bottomSheetBinding.edtAmount.setText(amount.toString())

            } else {
                bottomSheetBinding.edtAmount.setText(amount.toString())
            }
        }

        bottomSheetBinding.btnSave.setOnClickListener {
            product.costPrice = bottomSheetBinding.edtPrice.rawValue.toDouble() / 100
            product.save()

            // Incrementa a quantidade do estoque do produto
            val stock = Stock(product.id, amount)
            stock.increment(amount)

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    /**
     * @author Arley Santana
     * Decrementa a quantidade do estoque do produto
     */
    private fun updateStock(product: Product, amount: Int) {
        FirebaseHelper
            .getDatabase()
            .child("stock")
            .child(FirebaseHelper.getIdUser())
            .child(product.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stock = snapshot.getValue(Stock::class.java) as Stock

                    if (amount <= stock.amount) {
                        product.save()

                        stock.decrement(amount)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Estoque insuficiente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun listenerFragment() {
        parentFragmentManager.setFragmentResultListener(
            "KEY",
            this
        ) { key, bundle ->
            val product: Product = bundle[key] as Product

            // Armazena a lista atual do adapter
            val oldList = productAdapter.currentList

            // Gera uma nova lista a partir da lista antiga já com a tarefa atualizada
            val newList = oldList.toMutableList().apply {
                add(0, product)
            }

            // Envia a lista atualizada para o adapter
            productAdapter.submitList(newList)

            setPositionRecyclerView()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                val action = ProductsFragmentDirections
                    .actionMenuProductsToFormProductFragment(null)
                findNavController().navigate(action)
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