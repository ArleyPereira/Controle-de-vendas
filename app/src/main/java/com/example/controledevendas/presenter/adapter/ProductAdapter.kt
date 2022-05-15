package com.example.controledevendas.presenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.controledevendas.R
import com.example.controledevendas.data.model.Product
import com.example.controledevendas.databinding.ItemProductBinding
import com.example.controledevendas.util.formatedPrice

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
            context.getString(R.string.text_formated_price, product.costPrice.formatedPrice())

        holder.binding.textPriceSale.text =
            context.getString(R.string.text_formated_price, product.salePrice.formatedPrice())

        holder.binding.textStock.text = product.stock.toString()
        holder.binding.textSold.text = product.sold.toString()

        holder.binding.ibOption.setOnClickListener { productSelected(product, TypeSelected.Option) }
        holder.binding.llStock.setOnClickListener { productSelected(product, TypeSelected.Stock) }
        holder.binding.llSold.setOnClickListener { productSelected(product, TypeSelected.Sold) }
    }

    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

}

sealed class TypeSelected {
    object Option : TypeSelected()
    object Stock : TypeSelected()
    object Sold : TypeSelected()
}