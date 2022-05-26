package br.com.hellodev.controledevendas.presenter.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.databinding.FragmentAccountBinding
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.showBottomSheet

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.llLogout.setOnClickListener {
            showBottomSheet(
                titleDialog = R.string.text_title_dialog_logout_fragment,
                message = getString(R.string.text_message_dialog_logout_fragment),
                titleButton = R.string.text_button_ok_dialog_logout_fragment,
                buttonCancel = true,
                onOkClick = { logout() }
            )
        }

        binding.llProfile.setOnClickListener {
            findNavController().navigate(R.id.action_menu_account_to_profileFragment)
        }
    }

    private fun logout() {
        FirebaseHelper.getAuth().signOut()
        findNavController().navigate(R.id.action_menu_account_to_authentication)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}