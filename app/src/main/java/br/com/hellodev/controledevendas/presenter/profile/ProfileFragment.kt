package br.com.hellodev.controledevendas.presenter.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.hellodev.controledevendas.R
import br.com.hellodev.controledevendas.data.model.Product
import br.com.hellodev.controledevendas.data.model.User
import br.com.hellodev.controledevendas.databinding.BottomSheetAddImageProfileBinding
import br.com.hellodev.controledevendas.databinding.BottomSheetMoreProductBinding
import br.com.hellodev.controledevendas.databinding.ProfileFragmentBinding
import br.com.hellodev.controledevendas.presenter.products.ProductsFragmentDirections
import br.com.hellodev.controledevendas.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProfileFragment : BaseFragment() {

    private lateinit var viewModel: ProfileViewModel

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        getProfile()

        initListeners()
    }

    private fun initListeners() {
        binding.cardPhoto.setOnClickListener { showBottomAddPhoto() }

        binding.btnSave.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val establishment = binding.edtEstablishment.text.toString().trim()

        if (establishment.isNotEmpty()) {
            if (user != null) {
                hideKeyboard()

                binding.progressBar.isVisible = true

                user?.establishment = establishment

                saveProfile()
            } else {
                requireContext().toast(R.string.text_loading_data_profile_fragment)
            }
        } else {
            showBottomSheet(message = getString(R.string.establishment_empty_register_fragment))
        }
    }

    private fun saveProfile() {
        FirebaseHelper.getDatabase()
            .child("users")
            .child(FirebaseHelper.getIdUser())
            .setValue(user)
            .addOnSuccessListener {

                binding.progressBar.visibility = View.GONE
                snackBar(R.string.text_save_sucess_profile_fragment)

            }.addOnFailureListener {

                binding.progressBar.visibility = View.GONE
                showBottomSheet(message = getString(R.string.error_generic))

            }
    }

    private fun getProfile() {
        FirebaseHelper.getDatabase()
            .child("users")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java) as User

                    configData()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun configData() {
        user.let {
            if (it != null) {
                binding.edtEstablishment.setText(it.establishment)
                binding.edtEmail.setText(it.email)
            }
        }
    }

    private fun showBottomAddPhoto() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding: BottomSheetAddImageProfileBinding =
            BottomSheetAddImageProfileBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.btnCamera.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.btnGallery.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}