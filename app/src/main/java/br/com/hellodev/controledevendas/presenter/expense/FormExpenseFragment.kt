package br.com.hellodev.controledevendas.presenter.expense

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Expense
import br.com.hellodev.controledevendas.databinding.FragmentExpenseFormBinding
import br.com.hellodev.controledevendas.util.*
import java.util.*

class FormExpenseFragment : BaseFragment() {

    private var _binding: FragmentExpenseFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var expense: Expense
    private var newExpense: Boolean = true

    private val args: FormExpenseFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.expense.let {
            if (it != null) {
                this.expense = it

                newExpense = false

                configData()
            }
        }
    }

    private fun configData() {
        binding.editDescription.setText(expense.description)
        binding.editAmount.setText(expense.amount.toString())
    }

    private fun initListeners() {
        binding.editAmount.addTextChangedListener(MoneyMask(binding.editAmount))

        binding.btnSave.setOnClickListener { validadeData() }
    }

    private fun validadeData() {
        val description = binding.editDescription.text.toString().trim()
        val amount = MoneyMask.getValue(binding.editAmount.text.toString())

        if (description.isNotEmpty()) {
            if (amount > 0f) {

                hideKeyboard()

                binding.progressBar.isVisible = true

                if (newExpense) {
                    expense = Expense(
                        description = description,
                        amount = amount,
                        date = Calendar.getInstance().time.time
                    )
                } else {
                    expense.description = description
                    expense.amount = amount
                }

                saveExpense(expense)

            } else {
                showBottomSheet(message = "Informe o valor da despesa.")
            }
        } else {
            showBottomSheet(message = "Informe uma descrição.")
        }
    }

    private fun saveExpense(expense: Expense) {
        FirebaseHelper.getDatabase()
            .child("expenses")
            .child(FirebaseHelper.userId())
            .child(expense.id)
            .setValue(expense)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().popBackStack()
                }
            }.addOnFailureListener { exception ->
                showBottomSheet(message = exception.message.toString())
            }
    }

    private fun revemoExpense() {
        showBottomSheet(
            titleDialog = R.string.text_title_dialog_delete_expenses_fragment,
            message = getString(R.string.text_message_dialog_delete_expenses_fragment),
            titleButton = R.string.text_button_confirm_delete_expenses_fragment,
            onOkClick = {
                FirebaseHelper.getDatabase()
                    .child("expenses")
                    .child(FirebaseHelper.userId())
                    .child(this.expense.id)
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            snackBar(R.string.text_message_delete_sucess_expenses_fragment)

                            findNavController().popBackStack()
                        }
                    }.addOnFailureListener { exception ->
                        showBottomSheet(message = exception.message.toString())
                    }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_remove -> {
                revemoExpense()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_remove, menu)

        val item = menu.findItem(R.id.menu_remove)
        item.isVisible = !newExpense

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}