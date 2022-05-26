package com.planetway.demo.fudosan.ui.sign

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.planetway.demo.R
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory
import com.planetway.demo.fudosan.ui.getLanguage
import com.planetway.demo.fudosan.ui.toastShort
import com.planetway.planetid.rpsdk.PlanetId
import kotlinx.android.synthetic.main.fragment_sign.view.*


class SignFragment : Fragment() {

    private lateinit var binding: View
    private lateinit var viewModel: SignViewModel

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(SignViewModel::class.java)

        viewModel.signDataResultState.observe(this@SignFragment, Observer {
            if (it.success != null) {
                Toast.makeText(
                    context,
                    getString(R.string.success),
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.reset()

                findNavController().navigate(R.id.action_fragment_sign_to_signSuccessFragment)
            }

            if (it.error != null) {
                binding.loading.visibility = View.GONE
                binding.button_sign.isEnabled = true

                Toast.makeText(
                    context,
                    getString(it.error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding = inflater.inflate(R.layout.fragment_sign, container, false)

        binding.button_sign.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            binding.button_sign.isEnabled = false

            viewModel.sign()
        }

        if (getLanguage(context!!) == "ja") {
            binding.imageContract.setImageDrawable(context!!.getDrawable(R.drawable.contract_ja))
        } else {
            binding.imageContract.setImageDrawable(context!!.getDrawable(R.drawable.contract_en))
        }

        // TODO: Check localization?
        val personData = arguments?.get("personData") as Map<String, String>?
        if (personData != null) {
            binding.textViewPersonName.text = personData["name_en"]
            binding.textViewPersonDateOfBirth.text = personData["dob_en"]
            binding.textViewPersonAddress.text = personData["address_en"]
            binding.textViewPersonPhone.text = personData["phone"]
            binding.textViewPersonBankAccount.text = personData["bank_account"]
            binding.textViewPersonEmployer.text = personData["employer_en"]
        }

        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.getStringExtra("EXTRA_CALLBACK_ACTION") == PlanetIdAction.ACTION_SIGN.action) {
            handleCallbackUri()
            activity?.intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        }
    }

    private fun handleCallbackUri() {
        val callbackUriString = activity?.intent?.getStringExtra("EXTRA_CALLBACK_URI")
        try {
            val callbackUri = Uri.parse(callbackUriString)

            if (PlanetId.isRejected(callbackUri)) {
                Log.i("Sign", "Action rejected")

                toastShort("Rejected.")

                return
            }

            val error = PlanetId.getError(callbackUri)
            if (error != null) {
                Log.e("Sign", "Action error: $error")

                toastShort("Error.")

                return
            }

            viewModel.signCallback(PlanetId.getAuthResponse(callbackUri))
            binding.loading.visibility = View.VISIBLE
            binding.button_sign.isEnabled = false
        } catch (e: Exception) {
            Log.e("Sign", "Failed to handle callback url: $e")
        }
    }
}
