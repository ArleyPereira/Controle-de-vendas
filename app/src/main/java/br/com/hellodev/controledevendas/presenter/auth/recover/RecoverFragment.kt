package br.com.hellodev.controledevendas.presenter.auth.recover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.databinding.FragmentRecoverBinding
import br.com.hellodev.controledevendas.util.BaseFragment
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.initToolbar
import br.com.hellodev.controledevendas.util.showBottomSheet

class RecoverFragment : BaseFragment() {

    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
    }

    private fun initListeners() {
        binding.btnRecover.setOnClickListener {
            val email = binding.edtEmail.text.trim().toString()

            if (email.isNotEmpty()) {

                hideKeyboard()

                binding.progressBar.isVisible = true

                recoverAccount(email)

            } else {
                showBottomSheet(message = getString(R.string.email_empty_recover_fragment))
            }
        }
    }

    private fun recoverAccount(email: String) {
        FirebaseHelper
            .getAuth()
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                binding.progressBar.isVisible = false
                showBottomSheet(message = getString(R.string.text_send_sucess_recover_fragment))
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottomSheet(message = getString(FirebaseHelper.validateError(it.message.toString())))
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}