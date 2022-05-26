package com.planetway.demo.fudosan.ui.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.planetway.demo.R
import com.planetway.demo.databinding.FragmentCreateAccountBinding
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory
import com.planetway.demo.fudosan.ui.afterTextChanged


class CreateAccountFragment : Fragment() {

    private lateinit var binding: FragmentCreateAccountBinding
    private lateinit var viewModel: CreateAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(CreateAccountViewModel::class.java)

        viewModel.enterCredentialsFormState.observe(this@CreateAccountFragment, Observer {
            binding.buttonNext.isEnabled = it.isDataValid

            if (it.isDataValid) {
                binding.editUser.error = null
                binding.editPassword.error = null
                binding.editVerifyPassword.error = null
            } else {
                if (it.usernameError != null) {
                    binding.editUser.error = getString(it.usernameError)
                }

                if (it.passwordError != null) {
                    binding.editPassword.error = getString(it.passwordError)
                }

                if (it.verifyPasswordError != null) {
                    binding.editVerifyPassword.error = getString(it.verifyPasswordError)
                }
            }
        })

        viewModel.createAccountResultState.observe(this@CreateAccountFragment, Observer {
            binding.loading.visibility = View.GONE

            if (it.success != null) {
                findNavController()
                    .navigate(R.id.action_createAccountFragment_to_accountCreatedFragment)
            }

            if (it.error != null) {
                binding.buttonNext.isEnabled = true
                binding.editUser.isEnabled = true
                binding.editPassword.isEnabled = true
                binding.editVerifyPassword.isEnabled = true

                Toast.makeText(
                    context,
                    getString(it.error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner =
            this // Otherwise view will not be updated when view model state changes.
        binding.viewModel = viewModel

        binding.editUser.afterTextChanged(this::updateCredentialsState)
        binding.editPassword.afterTextChanged(this::updateCredentialsState)
        binding.editVerifyPassword.apply {
            afterTextChanged(this@CreateAccountFragment::updateCredentialsState)

            setOnEditorActionListener { _, actionId, _ ->
                if (viewModel.enterCredentialsFormState.value?.isDataValid == true) {
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            createAccount()
                    }
                }
                false
            }
        }

        binding.buttonNext.setOnClickListener {
            createAccount()
        }

        return binding.root
    }

    private fun createAccount() {
        binding.loading.visibility = View.VISIBLE
        binding.buttonNext.isEnabled = false
        binding.editUser.isEnabled = false
        binding.editPassword.isEnabled = false
        binding.editVerifyPassword.isEnabled = false

        viewModel.createAccount(
            binding.editUser.text.toString(),
            binding.editPassword.text.toString()
        )
    }

    private fun updateCredentialsState(newValue: String) {
        viewModel.credentialsChanged(
            binding.editUser.text.toString(),
            binding.editPassword.text.toString(),
            binding.editVerifyPassword.text.toString()
        )
    }
}
