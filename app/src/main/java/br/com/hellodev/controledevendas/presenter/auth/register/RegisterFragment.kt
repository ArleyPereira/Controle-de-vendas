package br.com.hellodev.controledevendas.presenter.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.User
import br.com.hellodev.controledevendas.databinding.FragmentRegisterBinding
import br.com.hellodev.controledevendas.util.BaseFragment
import br.com.hellodev.controledevendas.util.FirebaseHelper
import br.com.hellodev.controledevendas.util.initToolbar
import br.com.hellodev.controledevendas.util.showBottomSheet

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
    }

    private fun initListeners() {
        binding.btnRegister.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val establishment = binding.edtEstablishment.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (establishment.isNotEmpty()) {
            if (email.isNotEmpty()) {
                if (password.isNotEmpty()) {

                    hideKeyboard()

                    binding.progressBar.isVisible = true

                    createAccount(
                        User(
                            establishment = establishment,
                            email = email,
                            password = password
                        )
                    )

                } else {
                    showBottomSheet(message = getString(R.string.password_empty_register_fragment))
                }
            } else {
                showBottomSheet(message = getString(R.string.email_empty_register_fragment))
            }
        } else {
            showBottomSheet(message = getString(R.string.establishment_empty_register_fragment))
        }
    }

    private fun createAccount(user: User) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
            user.email, user.password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                user.id = FirebaseHelper.getAuth().currentUser!!.uid

                saveProfile(user)
            } else {
                binding.progressBar.visibility = View.GONE
                showBottomSheet(message = getString(FirebaseHelper.validateError(task.exception?.message.toString())))
            }
        }
    }

    private fun saveProfile(user: User) {
        FirebaseHelper
            .getDatabase()
            .child("users")
            .child(user.id)
            .setValue(user).addOnSuccessListener {
                findNavController().navigate(R.id.action_global_menu_main)
            }.addOnFailureListener(requireActivity()) {
                binding.progressBar.visibility = View.GONE
                showBottomSheet(message = getString(R.string.error_generic))
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}