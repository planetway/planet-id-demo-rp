package com.planetway.demo.fudosan.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.planetway.demo.R
import com.planetway.demo.databinding.FragmentLoginBinding
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.ui.*
import com.planetway.planetid.rpsdk.PlanetId.getError
import com.planetway.planetid.rpsdk.PlanetId.isRejected
import com.planetway.planetid.rpsdk.PlanetId.getAuthResponse


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(LoginViewModel::class.java)

        viewModel.apply {
            loginFormState.observe(this@LoginFragment, Observer {
                binding.buttonLogin.isEnabled = it.isDataValid
            })

            loginResultState.observe(this@LoginFragment, Observer {
                if (it.success != null) {
                    toastShort(getString(R.string.login_success))

                    startActivity(Intent(context, MainActivity::class.java).apply {
                        flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                    })

                    activity?.finish()
                }

                if (it.error != null) {
                    binding.loading.visibility = View.GONE
                    binding.buttonLoginWithPid.isEnabled = true
                    binding.editUser.isEnabled = true
                    binding.editPassword.isEnabled = true
                    binding.editUser.text.clear()
                    binding.editPassword.text.clear()

                    when (it.error) {
                        R.string.planet_id_not_linked -> {
                            AlertDialog.Builder(context)
                                .setTitle(R.string.error)
                                .setMessage(R.string.planet_id_not_linked)
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show()
                        }
                        R.string.planet_id_app_not_installed -> {
                            showPlanetIdAppNotInstalledAlert()
                        }
                        else -> {
                            toastShort(getString(it.error))
                        }
                    }
                }
            })
        }

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.buttonLoginWithPid.setOnClickListener {
            setLoading()

            viewModel.loginWithPlanetId()
        }

        binding.editUser.afterTextChanged(this::updateCredentialsState)
        binding.editPassword.apply {
            afterTextChanged(this@LoginFragment::updateCredentialsState)

            setOnEditorActionListener { _, actionId, _ ->
                if (viewModel.loginFormState.value?.isDataValid == true) {
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            loginWithPassword()
                    }
                }
                false
            }
        }

        binding.buttonLogin.setOnClickListener {
            loginWithPassword()
        }

        binding.textForgotPassword.setOnClickListener {
        }

        binding.textSignUp.setOnClickListener {
            viewModel.reset()

            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.getStringExtra("EXTRA_CALLBACK_ACTION") == PlanetIdAction.ACTION_LOGIN.action) {
            handleCallbackUri()
            activity?.intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        }

        viewModel.loginWithPassword(
            "margus.raim@planetway.com",
            "123123"
        )
    }

    private fun updateCredentialsState(newValue: String) {
        viewModel.credentialsChanged(
            binding.editUser.text.toString(),
            binding.editPassword.text.toString()
        )
    }

    private fun loginWithPassword() {
        setLoading()

        viewModel.loginWithPassword(
            binding.editUser.text.toString(),
            binding.editPassword.text.toString()
        )
    }

    private fun setLoading() {
        binding.loading.visibility = View.VISIBLE
        binding.buttonLoginWithPid.isEnabled = false
        binding.buttonLogin.isEnabled = false
        binding.editUser.isEnabled = false
        binding.editPassword.isEnabled = false
    }

    private fun handleCallbackUri() {
        val callbackUriString = activity?.intent?.getStringExtra("EXTRA_CALLBACK_URI")
        try {
            val callbackUri = Uri.parse(callbackUriString)

            if (isRejected(callbackUri)) {
                Log.i("LoginFrag", "Action rejected")

                toastShort("Rejected.")

                return
            }

            val error = getError(callbackUri)
            if (error != null) {
                Log.e("LoginFrag", "Action error: $error")

                toastShort("Error.")

                return
            }

            viewModel.loginWithPlanetIdCallback(getAuthResponse(callbackUri))

            setLoading()
        } catch (e: Exception) {
            Log.e("LoginFrag", "Failed to handle callback url: $e")
        }
    }
}
