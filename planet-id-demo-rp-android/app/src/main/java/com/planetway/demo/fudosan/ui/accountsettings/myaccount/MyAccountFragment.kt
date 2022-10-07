package com.planetway.demo.fudosan.ui.accountsettings.myaccount

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.planetway.demo.R
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory
import com.planetway.demo.fudosan.ui.LoginActivity
import kotlinx.android.synthetic.main.fragment_account_settings_my_account.view.*


class MyAccountFragment : Fragment() {

    private lateinit var layout: View
    private lateinit var viewModel: MyAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(MyAccountViewModel::class.java)

        viewModel.apply {
            deleteAccountResultState.observe(this@MyAccountFragment, Observer {
                layout.loading.visibility = View.GONE

                if (it.success != null) {
                    Toast.makeText(
                        context,
                        R.string.account_deleted,
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })

                    activity?.finish()
                }

                if (it.error != null) {
                    Toast.makeText(
                        context,
                        getString(it.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            unlinkResultState.observe(this@MyAccountFragment, Observer {
                layout.loading.visibility = View.GONE

                if (it.success != null) {
                    Toast.makeText(
                        context,
                        "Planet ID unlinked successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    updateLinkAndUnlinkVisibility()
                }

                if (it.error != null) {
                    Toast.makeText(
                        context,
                        getString(it.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        layout =
            inflater.inflate(R.layout.fragment_account_settings_my_account, container, false)

        layout.apply {
            textLinkWithPlanetID.setOnClickListener { linkWithPlanetID() }
            textUnlinkWithPlanetID.setOnClickListener { showUnlinkAlert() }
            textPersonalDataFromLra.setOnClickListener { personalDataFromLra() }
            textSignedDocumentList.setOnClickListener {
                findNavController().navigate(R.id.action_myAccountFragment_to_signedDocumentsFragment)
            }
            textDeleteAccount.setOnClickListener { showDeleteAccountAlert() }
        }

        updateLinkAndUnlinkVisibility()

        return layout
    }

    private fun updateLinkAndUnlinkVisibility() {
        if (viewModel.isPlanetIdLinked()) {
            layout.textLinkWithPlanetID.visibility = View.GONE
            layout.textUnlinkWithPlanetID.visibility = View.VISIBLE
            updateTextViewEnabled(layout.textPersonalDataFromLra, true)
            layout.textPlanetId.text = viewModel.getLocalUserInfo().planetId
        } else {
            layout.textLinkWithPlanetID.visibility = View.VISIBLE
            layout.textUnlinkWithPlanetID.visibility = View.GONE
            updateTextViewEnabled(layout.textPersonalDataFromLra, false)
            layout.textPlanetId.text = ""
        }
    }

    private fun updateTextViewEnabled(textView: TextView, enabled: Boolean) {
        textView.isClickable = enabled
        textView.setTextColor(
            AppCompatResources.getColorStateList(
                context!!,
                if (enabled) R.color.textNormal else R.color.textDisabled
            )
        )
    }

    private fun linkWithPlanetID() {
        findNavController().navigate(R.id.action_myAccountFragment_to_linkPlanetIdFragment)
    }

    private fun personalDataFromLra() {
        findNavController().navigate(R.id.action_myAccountFragment_to_dataFromLraFragment)
    }

    private fun showDeleteAccountAlert() {
        AlertDialog.Builder(context)
            .setTitle(R.string.delete_account)
            .setMessage(R.string.delete_account_confirmation)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                layout.loading.visibility = View.VISIBLE

                viewModel.deleteAccount()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showUnlinkAlert() {
        AlertDialog.Builder(context)
            .setTitle(R.string.unlink_with_planet_id)
            .setMessage(R.string.unlink_confirmation)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                layout.loading.visibility = View.VISIBLE

                viewModel.unlink()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}
