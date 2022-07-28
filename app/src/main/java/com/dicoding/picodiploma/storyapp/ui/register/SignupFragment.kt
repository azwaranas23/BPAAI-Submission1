package com.dicoding.picodiploma.storyapp.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private lateinit var _binding: FragmentSignupBinding
    private val binding get() = _binding
    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]
        viewModel.isSignupSuccess.observe(viewLifecycleOwner) {
            if (it) toLogin(true)
        }
        viewModel.toastText.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { toastText ->
                showToast(toastText)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        binding.btnSignUp.setOnClickListener {
            tryRegister()
        }

        binding.tvLogIn.setOnClickListener {
            toLogin(false)
        }
    }

    private fun tryRegister() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val isNameInvalid = name.isEmpty()
        val isEmailInvalid = binding.etEmail.isError || email.isEmpty()
        val isPasswordInvalid = binding.etPassword.isError || password.isEmpty()
        when {
            isNameInvalid -> {
                binding.etName.error = getString(R.string.invalid_name)
                binding.etName.requestFocus()
            }
            isEmailInvalid -> binding.etEmail.requestFocus()
            isPasswordInvalid -> binding.etPassword.requestFocus()
            else -> viewModel.register(name, email, password)
        }
    }

    private fun toLogin(isRegistered: Boolean) {
        val toLoginFragment =
            SignupFragmentDirections.actionSignupFragmentToLoginFragment()
        if (isRegistered) {
            toLoginFragment.email = binding.etEmail.text.toString()
            toLoginFragment.password = binding.etPassword.text.toString()
        }
        view?.findNavController()?.navigate(toLoginFragment)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.etName.isEnabled = false
            binding.etEmail.isEnabled = false
            binding.etPassword.isEnabled = false
            binding.btnSignUp.isEnabled = false
            binding.tvLogIn.isEnabled = false
            binding.pbRegister.visibility = View.VISIBLE
        } else {
            binding.etName.isEnabled = true
            binding.etEmail.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.btnSignUp.isEnabled = true
            binding.tvLogIn.isEnabled = true
            binding.pbRegister.visibility = View.GONE
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(view?.context, text, Toast.LENGTH_SHORT).show()
    }
}