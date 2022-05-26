package com.planetway.demo.fudosan.ui

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.planetway.demo.R
import com.planetway.demo.fudosan.data.exception.ApiException
import com.planetway.demo.fudosan.data.exception.NetworkException
import com.planetway.demo.fudosan.data.exception.UnauthorizedException

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun Fragment.showPlanetIdAppNotInstalledAlert() {
    AlertDialog.Builder(context)
        .setTitle(R.string.error)
        .setMessage(R.string.planet_id_app_not_installed)
        .setPositiveButton(android.R.string.ok, null)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
}

fun translate(exception: Exception): Int {
    return when (exception) {
        is UnauthorizedException -> R.string.unauthorized
        is NetworkException -> R.string.network_error
        is ApiException -> when (exception.message) {
            "PLANETID_NOT_LINKED" -> R.string.planet_id_not_linked
            else -> R.string.something_went_wrong
        }
        is ActivityNotFoundException -> R.string.planet_id_app_not_installed
        else -> R.string.something_went_wrong
    }
}

fun getLanguage(context: Context): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0].language
    } else {
        context.resources.configuration.locale.language
    }
}

fun Fragment.toastShort(text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}
