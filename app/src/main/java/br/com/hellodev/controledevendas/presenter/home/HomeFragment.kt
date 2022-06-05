package br.com.hellodev.controledevendas.presenter.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Expense
import br.com.hellodev.controledevendas.data.model.Sale
import br.com.hellodev.controledevendas.databinding.FragmentHomeBinding
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.formatedValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var totalExpenses: Float = 0f
    private var totalSales: Float = 0f
    private var totalProducts: Int = 0

    private var salesRef: DatabaseReference? = null
    private lateinit var salesEventListener: ValueEventListener

    private var expensesRef: DatabaseReference? = null
    private lateinit var expensesEventListener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        // Recupera todos os dados do Firebase
        getData(TypeDateHistoric.Today)
    }

    // Recupera todos os dados do Firebase
    private fun getData(typeDateHistoric: TypeDateHistoric) {
        getSales(typeDateHistoric)

        getExpenses(typeDateHistoric)
    }

    private fun initListeners() {
        binding.textToday.setOnClickListener {
            setStateFavoriteTexts(binding.textToday.id)

            getData(TypeDateHistoric.Today)
        }

        binding.textWeek.setOnClickListener {
            setStateFavoriteTexts(binding.textWeek.id)

            getData(TypeDateHistoric.Week)
        }

        binding.textMonth.setOnClickListener {
            setStateFavoriteTexts(binding.textMonth.id)

            getData(TypeDateHistoric.Month)
        }
    }

    private fun getSales(typeDateHistoric: TypeDateHistoric) {
        totalSales = 0f
        totalProducts = 0

        binding.textAmountSales.isVisible = false
        binding.progressSales.isVisible = true

        binding.textAmountProducts.isVisible = false
        binding.progressAmountProducts.isVisible = true

        salesRef = FirebaseHelper
            .getDatabase()
            .child("sales")
            .child(FirebaseHelper.userId())
        salesEventListener = salesRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val sale = ds.getValue(Sale::class.java) as Sale

                        val currentDate = Date(Calendar.getInstance().time.time)
                        val compareDate = Date(sale.date)

                        when (typeDateHistoric) {
                            TypeDateHistoric.Today -> { // Faturamento do dia
                                if (isSameDay(currentDate, compareDate)) {
                                    totalSales += (sale.amount * sale.currentPrice)
                                    totalProducts += sale.amount
                                }
                            }
                            TypeDateHistoric.Week -> { // Faturamento da semana
                                if (isSameWeek(currentDate.time, compareDate.time)) {
                                    totalSales += (sale.amount * sale.currentPrice)
                                    totalProducts += sale.amount
                                }
                            }
                            else -> { // Faturamento do mês
                                if (isSameMonth(currentDate, compareDate)) {
                                    totalSales += (sale.amount * sale.currentPrice)
                                    totalProducts += sale.amount
                                }
                            }
                        }
                    }
                }


                binding.textAmountSales.isVisible = true
                binding.textAmountSales.text =
                    getString(R.string.text_formated_price, totalSales.formatedValue())
                binding.progressSales.isVisible = false

                binding.textAmountProducts.isVisible = true
                binding.textAmountProducts.text = totalProducts.toString()
                binding.progressAmountProducts.isVisible = false

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    /**
     * @author Arley Santana
     * currentDate -> Data atual
     * currentDate -> Data a ser comparada com a data atual
     */
    private fun getExpenses(typeDateHistoric: TypeDateHistoric) {
        totalExpenses = 0f

        binding.textAmountExpenses.isVisible = false
        binding.progressExpenses.isVisible = true

        binding.textAmount.isVisible = false
        binding.progressAmount.isVisible = true

        expensesRef = FirebaseHelper
            .getDatabase()
            .child("expenses")
            .child(FirebaseHelper.userId())
        expensesEventListener = expensesRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val expense = ds.getValue(Expense::class.java) as Expense

                        val currentDate = Date(Calendar.getInstance().time.time)
                        val compareDate = Date(expense.date)

                        when (typeDateHistoric) {
                            TypeDateHistoric.Today -> { // Faturamento do dia
                                if (isSameDay(currentDate, compareDate)) {
                                    totalExpenses += expense.amount
                                }
                            }
                            TypeDateHistoric.Week -> { // Faturamento da semana
                                if (isSameWeek(currentDate.time, compareDate.time)) {
                                    totalExpenses += expense.amount
                                }
                            }
                            else -> { // Faturamento do mês
                                if (isSameMonth(currentDate, compareDate)) {
                                    totalExpenses += expense.amount
                                }
                            }
                        }
                    }
                }

                binding.textAmountExpenses.isVisible = true
                binding.textAmountExpenses.text =
                    getString(R.string.text_formated_price, totalExpenses.formatedValue())
                binding.progressExpenses.isVisible = false

                // Calcula o total das vendas - o total das despesas
                calculateAmount()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Calcula o total das vendas - o total das despesas
    private fun calculateAmount() {
        val colorPositive = ContextCompat.getColor(requireContext(), R.color.sucess)
        val colorNegative = ContextCompat.getColor(requireContext(), R.color.error)

        val amount = totalSales - totalExpenses
        binding.textAmount.apply {
            setTextColor(if (amount >= 0) colorPositive else colorNegative)
            isVisible = true
            text = getString(R.string.text_formated_price, amount.formatedValue())
        }

        binding.progressAmount.isVisible = false
    }

    /**
     * @author Arley Santana
     * currentDate -> Data atual
     * currentDate -> Data a ser comparada com a data atual
     */
    private fun isSameWeek(currentDate: Long, compareDate: Long): Boolean {

        // Data atual
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentDate
        val currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR)

        // Data a ser comparada com a data atual
        val compareCalendar = Calendar.getInstance()
        compareCalendar.timeInMillis = compareDate
        val compareWeek = compareCalendar.get(Calendar.WEEK_OF_YEAR)

        return currentWeek == compareWeek
    }

    /**
     * @author Arley Santana
     * currentDate -> Data atual
     * currentDate -> Data a ser comparada com a data atual
     * Verifica se as transações são no mesmo dia
     */
    private fun isSameDay(currentDate: Date, compareDate: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale("pt", "BR"))
        return fmt.format(currentDate).equals(fmt.format(compareDate))
    }

    /**
     * @author Arley Santana
     * currentDate -> Data atual
     * currentDate -> Data a ser comparada com a data atual
     * Verifica se as transações são no mesmo mês
     */
    private fun isSameMonth(currentDate: Date, compareDate: Date): Boolean {
        val fmt = SimpleDateFormat("MM", Locale("pt", "BR"))
        return fmt.format(currentDate).equals(fmt.format(compareDate))
    }

    // Configura a cor e font dos textos
    private fun setStateFavoriteTexts(idTextSelected: Int) {
        val colorSelected = ContextCompat.getColor(requireContext(), R.color.sucess)
        val colorUnselected = ContextCompat.getColor(requireContext(), R.color.color_hint)

        val fontTextSelected = ResourcesCompat.getFont(requireContext(), R.font.avenir_black)
        val fontUnselected = ResourcesCompat.getFont(requireContext(), R.font.avenir_medium)

        binding.textToday.apply {
            setTextColor(if (id == idTextSelected) colorSelected else colorUnselected)
            typeface = if (id == idTextSelected) fontTextSelected else fontUnselected
        }

        binding.textWeek.apply {
            setTextColor(if (id == idTextSelected) colorSelected else colorUnselected)
            typeface = if (id == idTextSelected) fontTextSelected else fontUnselected
        }

        binding.textMonth.apply {
            setTextColor(if (id == idTextSelected) colorSelected else colorUnselected)
            typeface = if (id == idTextSelected) fontTextSelected else fontUnselected
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (salesRef != null) salesRef!!.removeEventListener(salesEventListener)
        if (expensesRef != null) expensesRef!!.removeEventListener(expensesEventListener)
        _binding = null
    }

}

sealed class TypeDateHistoric {
    object Today : TypeDateHistoric()
    object Week : TypeDateHistoric()
    object Month : TypeDateHistoric()
}