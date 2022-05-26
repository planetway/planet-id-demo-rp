package com.planetway.demo.fudosan.ui.accountcreated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.planetway.demo.R
import kotlinx.android.synthetic.main.fragment_account_created.view.*


class AccountCreatedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_account_created, container, false)

        inflate.button_backToLogin.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_accountCreatedFragment_to_loginFragment)
        )

        return inflate
    }
}
