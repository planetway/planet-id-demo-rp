package com.planetway.demo.fudosan.ui.accountsettings.myaccount.link

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.planetway.demo.R
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory
import com.planetway.demo.fudosan.ui.toastShort
import com.planetway.planetid.rpsdk.PlanetId
import kotlinx.android.synthetic.main.fragment_account_settings_link_planetid.view.*
import kotlinx.android.synthetic.main.fragment_account_settings_my_account.view.loading


class LinkPlanetIdFragment : Fragment() {

    private lateinit var inflate: View
    private lateinit var viewModel: LinkPlanetIdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(LinkPlanetIdViewModel::class.java)

        viewModel.apply {
            linkPlanetIdResultState.observe(this@LinkPlanetIdFragment, Observer {
                if (it.success != null) {
                    inflate.loading.visibility = View.GONE
                    inflate.textLoading.visibility = View.GONE

                    inflate.imageSuccess.visibility = View.VISIBLE
                    inflate.textSuccess.visibility = View.VISIBLE
                }

                if (it.error != null) {
                    handleError(it.error)
                }
            })
        }

        inflate =
            inflater.inflate(R.layout.fragment_account_settings_link_planetid, container, false)

        inflate.loading.visibility = View.VISIBLE
        inflate.textLoading.visibility = View.VISIBLE

        return inflate
    }

    private fun handleError(error: Int) {
        inflate.loading.visibility = View.GONE
        inflate.textLoading.visibility = View.GONE

        inflate.imageError.visibility = View.VISIBLE
        inflate.textError.text = getString(error)
        inflate.textError.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.getStringExtra("EXTRA_CALLBACK_ACTION") == PlanetIdAction.ACTION_LINK.action) {
            handleCallbackUri()
            activity?.intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        } else {
            viewModel.linkWithPlanetID()
        }
    }

    private fun handleCallbackUri() {
        val callbackUriString = activity?.intent?.getStringExtra("EXTRA_CALLBACK_URI")
        try {
            val callbackUri = Uri.parse(callbackUriString)

            if (PlanetId.isRejected(callbackUri)) {
                Log.i("LinkFrag", "Action rejected")

                handleError(R.string.something_went_wrong)
                toastShort("Rejected.")

                return
            }

            val error = PlanetId.getError(callbackUri)
            if (error != null) {
                Log.e("LinkFrag", "Action error: $error")

                handleError(R.string.something_went_wrong)
                toastShort("Error.")

                return
            }

            viewModel.linkCallback(PlanetId.getAuthResponse(callbackUri))
        } catch (e: Exception) {
            Log.e("LinkFrag", "Failed to handle callback url: $e")
        }
    }
}
