package br.com.hellodev.controledevendas.presenter.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.hellodev.controledevendas.databinding.FragmentFormProductBinding
import br.com.hellodev.controledevendas.util.MoneyTextWatcher
import br.com.hellodev.controledevendas.util.getValueFormated
import br.com.hellodev.controledevendas.util.initToolbar
import br.com.hellodev.controledevendas.util.showBottomSheet

class FormProductFragment : Fragment() {

    private var _binding: FragmentFormProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
    }

    private fun initListeners() {
        binding.editCostPrice.addTextChangedListener(MoneyTextWatcher(binding.editCostPrice))
        binding.editSalePrice.addTextChangedListener(MoneyTextWatcher(binding.editSalePrice))

        binding.btnSave.setOnClickListener { validadeData() }
    }

    private fun validadeData() {
        val name = binding.editName.text.toString().trim()
        val cost = binding.editCostPrice.text.toString().getValueFormated()
        val sale = binding.editSalePrice.text.toString().getValueFormated()

        if (name.isNotEmpty()) {
            if (cost > 0f) {
                if (sale > 0f) {

                    Toast.makeText(requireContext(), "tudo certo!", Toast.LENGTH_SHORT).show()

                } else {
                    showBottomSheet(message = "Informe o preço de venda  do produto.")
                }
            } else {
                showBottomSheet(message = "Informe o preço de custo do produto.")
            }
        } else {
            showBottomSheet(message = "Informe o nome do produto.")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}