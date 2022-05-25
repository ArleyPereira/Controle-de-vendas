package br.com.hellodev.controledevendas.presenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Product
import br.com.hellodev.controledevendas.databinding.ItemProductBinding
import br.com.hellodev.controledevendas.util.formatedValue

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

        holder.binding.textStock.text = product.amount.toString()
        holder.binding.textSold.text = product.sold.toString()

        holder.binding.ibOption.setOnClickListener { productSelected(product, TypeSelected.Option) }
        holder.binding.llStock.setOnClickListener { productSelected(product, TypeSelected.Stock) }
        holder.binding.llSold.setOnClickListener { productSelected(product, TypeSelected.Sold) }
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