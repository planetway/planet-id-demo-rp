package com.planetway.demo.fudosan.ui.apply

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.planetway.demo.R
import com.planetway.demo.databinding.FragmentApplyBinding
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory
import com.planetway.demo.fudosan.ui.toastShort
import com.planetway.planetid.rpsdk.PlanetId


class ApplyFragment : Fragment() {

    private lateinit var viewModel: ApplyViewModel
    private lateinit var binding: FragmentApplyBinding
    private var selectedDataBank: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(ApplyViewModel::class.java)

        viewModel.retrieveDataResultState.observe(this@ApplyFragment, Observer {
            if (it.personData != null) {
                Toast.makeText(
                    context,
                    getString(R.string.success),
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.reset()

                findNavController().navigate(
                    R.id.action_fragment_apply_to_fragment_sign,
                    bundleOf(Pair("personData", it.personData))
                )
            }

            if (it.error != null) {
                binding.loading.visibility = View.GONE
                binding.buttonApply.isEnabled = true

                Toast.makeText(
                    context,
                    getString(it.error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding = FragmentApplyBinding.inflate(inflater, container, false)

        binding.buttonApply.apply {
            isEnabled = false

            setOnClickListener {
                selectedDataBank?.let {
                    binding.loading.visibility = View.VISIBLE
                    isEnabled = false

                    viewModel.apply(it)
                }
            }
        }

        for (provider in binding.providers.iterator()) {
            provider.setOnClickListener(this::selectProvider)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.getStringExtra("EXTRA_CALLBACK_ACTION") == PlanetIdAction.ACTION_DATA_BANK_CONSENT.action) {
            handleCallbackUri()
            activity?.intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        }
    }

    private fun selectProvider(view: View) {
        for (provider in binding.providers.iterator()) {
            provider.background = null
        }

        view.background =
            AppCompatResources.getDrawable(context!!, R.color.colorAccentLight)

        if (binding.loading.visibility != View.VISIBLE) {
            binding.buttonApply.isEnabled = true
        }

        selectedDataBank = view.tag as String
    }

    private fun handleCallbackUri() {
        val callbackUriString = activity?.intent?.getStringExtra("EXTRA_CALLBACK_URI")
        try {
            val callbackUri = Uri.parse(callbackUriString)

            val dataBank = callbackUri.getQueryParameter("data-bank")
            for (provider in binding.providers.iterator()) {
                if (provider.tag == dataBank) {
                    selectProvider(provider)
                }
            }

            if (PlanetId.isRejected(callbackUri)) {
                Log.i("ApplyFrag", "Action rejected")

                toastShort("Rejected.")

                return
            }

            val error = PlanetId.getError(callbackUri)
            if (error != null) {
                Log.e("ApplyFrag", "Action error: $error")

                toastShort("Error.")

                return
            }

            viewModel.consentCallback(
                callbackUri.getQueryParameter("data-bank")!!,
                PlanetId.getAuthResponse(callbackUri)
            )
            binding.loading.visibility = View.VISIBLE
            binding.buttonApply.isEnabled = false
        } catch (e: Exception) {
            Log.e("ApplyFrag", "Failed to handle callback uri: $e")
        }
    }
}
