package com.planetway.demo.fudosan.ui.accountsettings.myaccount.datafromlra

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
import com.planetway.demo.fudosan.ui.toastShort
import com.planetway.planetid.rpsdk.PlanetId
import kotlinx.android.synthetic.main.fragment_account_settings_personal_data_from_lra.view.*


class DataFromLraFragment : Fragment() {

    private lateinit var layout: View
    private lateinit var viewModel: DataFromLraViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(DataFromLraViewModel::class.java)

        viewModel.apply {
            dataFromLraResultState.observe(this@DataFromLraFragment, Observer {
                if (it.person != null) {
                    findNavController().navigate(
                        R.id.action_dataFromLraFragment_to_dataFromLraSuccessFragment
                    )
                }

                if (it.error != null) {
                    layout.loading.visibility = View.GONE
                    layout.buttonOk.isEnabled = true

                    Toast.makeText(
                        context,
                        getString(it.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        layout =
            inflater.inflate(
                R.layout.fragment_account_settings_personal_data_from_lra,
                container,
                false
            )

        layout.buttonOk.setOnClickListener {
            layout.buttonOk.isEnabled = false
            layout.loading.visibility = View.VISIBLE

            viewModel.getPerson()
        }

        layout.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.getStringExtra("EXTRA_CALLBACK_ACTION") == PlanetIdAction.ACTION_LRA_CONSENT.action) {
            handleCallbackUri()
            activity?.intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        }
    }

    private fun handleCallbackUri() {
        val callbackUriString = activity?.intent?.getStringExtra("EXTRA_CALLBACK_URI")
        try {
            val callbackUri = Uri.parse(callbackUriString)

            if (PlanetId.isRejected(callbackUri)) {
                Log.i("LraFrag", "Action rejected")

                toastShort("Rejected.")

                return
            }

            val error = PlanetId.getError(callbackUri)
            if (error != null) {
                Log.e("LraFrag", "Action error: $error")

                toastShort("Error.")

                return
            }

            viewModel.consentCallback(PlanetId.getAuthResponse(callbackUri))
        } catch (e: Exception) {
            Log.e("LraFrag", "Failed to handle callback uri: $e")
        }
    }
}
