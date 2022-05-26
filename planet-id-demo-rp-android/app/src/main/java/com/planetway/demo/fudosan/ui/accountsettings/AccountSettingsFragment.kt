package com.planetway.demo.fudosan.ui.accountsettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.planetway.demo.R
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory
import com.planetway.demo.fudosan.ui.LoginActivity
import kotlinx.android.synthetic.main.fragment_account_settings.view.*


class AccountSettingsFragment : Fragment() {

    private lateinit var layout: View
    private lateinit var viewModel: AccountSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(AccountSettingsViewModel::class.java)

        viewModel.logOutResultState.observe(this@AccountSettingsFragment, Observer {
            if (it.success != null) {
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

        layout = inflater.inflate(R.layout.fragment_account_settings, container, false)

        layout.textMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_accountSettingsFragment_to_myAccountFragment)
        }

        layout.textLogout.setOnClickListener {
            viewModel.logOut()
        }

        return layout
    }
}
