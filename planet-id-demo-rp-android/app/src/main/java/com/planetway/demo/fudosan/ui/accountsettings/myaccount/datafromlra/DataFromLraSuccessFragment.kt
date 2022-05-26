package com.planetway.demo.fudosan.ui.accountsettings.myaccount.datafromlra

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.planetway.demo.R
import com.planetway.demo.fudosan.DemoRpApplication
import kotlinx.android.synthetic.main.fragment_account_settings_personal_data_from_lra_success.view.*


class DataFromLraSuccessFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val accountRepository = (activity!!.application as DemoRpApplication).accountRepository
        return inflater.inflate(
            R.layout.fragment_account_settings_personal_data_from_lra_success,
            container,
            false
        ).apply {
            val person = accountRepository.getPeron()
            if (person != null) {
                textNameRomaji.text = "${person.firstNameRomaji} ${person.lastNameRomaji}"
                textNameKatakana.text = "${person.lastNameKatakana} ${person.firstNameKatakana}"
                textNameKanjii.text = "${person.lastNameKanji} ${person.firstNameKanji}"
                textViewBirthDate.text = person.dateOfBirth
                val address = person.address?.firstOrNull()
                if (address != null) {
                    textViewAddress.text = "${address.street}, ${address.postalCode}, ${address.city}, ${address.prefecture}"
                }
            } else {
                Log.d("LraSuccessFrag", "Person is null")
            }
        }
    }
}
