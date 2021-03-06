package br.com.hellodev.controledevendas.presenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Product
import br.com.hellodev.controledevendas.data.model.Sale
import br.com.hellodev.controledevendas.data.model.Stock
import br.com.hellodev.controledevendas.databinding.ItemProductBinding
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.formatedValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProductAdapter(
    private val context: Context,
    private val productSelected: (Product, TypeSelected) -> Unit
) : ListAdapter<Product, ProductAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)

        holder.binding.textProduct.text = product.name

        holder.binding.textPriceCost.text =
            context.getString(R.string.text_formated_price, product.costPrice.formatedValue())

        holder.binding.textPriceSale.text =
            context.getString(R.string.text_formated_price, product.salePrice.formatedValue())

        holder.binding.ibOption.setOnClickListener { productSelected(product, TypeSelected.Option) }
        holder.binding.llStock.setOnClickListener { productSelected(product, TypeSelected.Stock) }
        holder.binding.llSold.setOnClickListener { productSelected(product, TypeSelected.Sold) }

        // Recupera o estoque atual do produto
        getStock(holder, product.id)

        // Recupera as vendas do produto
        getSale(holder, product.id)
    }

    // Recupera o estoque atual do produto
    private fun getStock(holder: ViewHolder, idProduct: String) {
        FirebaseHelper
            .getDatabase()
            .child("stock")
            .child(FirebaseHelper.userId())
            .child(idProduct)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stock = snapshot.getValue(Stock::class.java) as Stock

                    holder.binding.progressStock.isVisible = false
                    holder.binding.textStock.isVisible = true
                    holder.binding.textStock.text = stock.amount.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    // Recupera as vendas do produto
    private fun getSale(holder: ViewHolder, idProduct: String) {
        FirebaseHelper
            .getDatabase()
            .child("sales")
            .child(FirebaseHelper.userId())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var saleAmount = 0
                    for (sn in snapshot.children) {
                        val sale = sn.getValue(Sale::class.java) as Sale

                        if (sale.idProduct == idProduct) {
                            saleAmount += sale.amount
                        }
                    }

                    holder.binding.progressSales.isVisible = false
                    holder.binding.textSale.isVisible = true
                    holder.binding.textSale.text = saleAmount.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: List<Product>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

}

sealed class TypeSelected {
    object Option : TypeSelected()
    object Stock : TypeSelected()
    object Sold : TypeSelected()
}