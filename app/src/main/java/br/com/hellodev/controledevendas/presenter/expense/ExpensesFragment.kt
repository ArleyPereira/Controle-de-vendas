package br.com.hellodev.controledevendas.presenter.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Expense
import br.com.hellodev.controledevendas.databinding.FragmentExpensesBinding
import br.com.hellodev.controledevendas.presenter.adapter.ExpenseAdapter
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.initToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        getExpenses()

        initListeners()
    }

    private fun initListeners() {
        binding.btnAddExpense.setOnClickListener { navigationFormExpense(null) }
    }

    private fun getExpenses() {
        FirebaseHelper.getDatabase()
            .child("expenses")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expenseList = mutableListOf<Expense>()
                    for (ds in snapshot.children) {
                        val expense = ds.getValue(Expense::class.java) as Expense
                        expenseList.add(expense)
                    }

                    expenseList.reverse()
                    initRecycler(expenseList)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun initRecycler(expenseList: List<Expense>) {
        listEmpty(expenseList)

        val expenseAdapter = ExpenseAdapter(requireContext(), expenseList) { expense ->
            navigationFormExpense(expense)
        }

        with(binding.rvExpenses) {
            setHasFixedSize(true)
            adapter = expenseAdapter
        }
    }

    private fun navigationFormExpense(expense: Expense? = null) {
        val action = ExpensesFragmentDirections
            .actionExpensesFragmentToFormExpensesFragment(expense)
        findNavController().navigate(action)
    }

    private fun listEmpty(expenseList: List<Expense>) {
        binding.textInfo.text = if (expenseList.isNotEmpty()) {
            ""
        } else {
            "Nenhuma despesa registrada."
        }

        binding.progressBar.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}