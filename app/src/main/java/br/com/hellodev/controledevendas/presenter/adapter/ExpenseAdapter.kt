package br.com.hellodev.controledevendas.presenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Expense
import br.com.hellodev.controledevendas.databinding.ItemExpenseBinding
import br.com.hellodev.controledevendas.util.formatDate
import br.com.hellodev.controledevendas.util.formatedValue

class ExpenseAdapter(
    private val context: Context,
    private val expenseList: List<Expense>,
    private val expenseSelected: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.binding.textDescription.text = expense.description

        holder.binding.textAmount.text =
            context.getString(R.string.text_formated_price, expense.amount.formatedValue())

        holder.binding.textDateExpense.text = expense.date.formatDate(1)

        holder.itemView.setOnClickListener { expenseSelected(expense) }
    }

    inner class ViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = expenseList.size

}