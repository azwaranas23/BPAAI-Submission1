package com.dicoding.picodiploma.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.preference.SessionModel
import com.dicoding.picodiploma.storyapp.preference.SessionPreferences
import com.dicoding.picodiploma.storyapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var mSessionPreferences: SessionPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel.session.observe(viewLifecycleOwner) {
            login(it)
        }
        viewModel.toastText.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { toastText ->
                showToast(toastText)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        mSessionPreferences = SessionPreferences(view.context)
        if (mSessionPreferences.getSession() != null) {
            view.findNavController().navigate(R.id.action_loginFragment_to_listStoryActivity)
            activity?.finish()
        }

        LoginFragmentArgs.fromBundle(arguments as Bundle).email?.let {
            binding.etEmail.setText(it)
        }
        LoginFragmentArgs.fromBundle(arguments as Bundle).password?.let {
            binding.etPassword.setText(it)
        }

        binding.btnLogin.setOnClickListener {
            tryLogin()
        }

        binding.tvSignUp.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_signupFragment)
        )

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.tvTitle, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edtPassword =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(100)
        val tvSignUp = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(tvRegister, tvSignUp)
        }

        AnimatorSet().apply {
            playSequentially(tvEmail, edtEmail, tvPassword, edtPassword, btnLogin, together)
            start()
        }
    }

    private fun tryLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val isEmailInvalid = binding.etEmail.isError || email.isEmpty()
        val isPasswordInvalid = binding.etPassword.isError || password.isEmpty()
        when {
            isEmailInvalid -> binding.etEmail.requestFocus()
            isPasswordInvalid -> binding.etPassword.requestFocus()
            else -> viewModel.login(email, password)
        }
    }

    private fun login(session: SessionModel) {
        mSessionPreferences.setSession(session)
        view?.findNavController()?.navigate(R.id.action_loginFragment_to_listStoryActivity)
        activity?.finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.etEmail.isEnabled = false
            binding.etPassword.isEnabled = false
            binding.btnLogin.isEnabled = false
            binding.tvSignUp.isEnabled = false
            binding.pbLogin.visibility = View.VISIBLE
        } else {
            binding.etEmail.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.btnLogin.isEnabled = true
            binding.tvSignUp.isEnabled = true
            binding.pbLogin.visibility = View.GONE
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(view?.context, text, Toast.LENGTH_SHORT).show()
    }
}