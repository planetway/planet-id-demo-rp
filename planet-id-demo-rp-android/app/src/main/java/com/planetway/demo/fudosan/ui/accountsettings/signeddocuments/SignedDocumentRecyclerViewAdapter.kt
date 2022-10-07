package com.planetway.demo.fudosan.ui.accountsettings.signeddocuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.planetway.demo.R
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.data.model.SignedDocument
import com.planetway.demo.fudosan.ui.getLanguage
import com.planetway.planetid.rpsdk.PlanetId


class SignedDocumentRecyclerViewAdapter(private val signedDocumentsViewModel: SignedDocumentsViewModel): RecyclerView.Adapter<SignedDocumentRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDocumentUuid: TextView
        val textDocumentType: TextView
        val btnRevoke: Button

        init {
            textDocumentUuid = view.findViewById(R.id.textDocumentUuid)
            textDocumentType = view.findViewById(R.id.textDocumentType)
            btnRevoke = view.findViewById(R.id.btnRevoke)
        }
    }

    private val signedDocuments = mutableListOf<SignedDocument>()

    init {
        if (signedDocumentsViewModel.signedDocuments.value != null) {
            this.signedDocuments.addAll(signedDocumentsViewModel.signedDocuments.value!!)
        }
    }
    fun updateList(signedDocuments: List<SignedDocument>) {
        this.signedDocuments.clear()
        this.signedDocuments.addAll(signedDocuments)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_signed_document, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = signedDocuments[position]
        holder.textDocumentUuid.text = doc.consentUuid ?: doc.uuid
        holder.textDocumentType.text = if (doc.revokeDocumentUuid != null) {
            "Consent (revoked)"
        } else if (doc.consentUuid != null) {
            "Consent"
        } else {
            "Document"
        }

        holder.btnRevoke.isVisible = doc.consentUuid != null && doc.revokeDocumentUuid == null

        if (holder.btnRevoke.isVisible) {
            holder.btnRevoke.setOnClickListener {
                signedDocumentsViewModel.getApplication<DemoRpApplication>().apiService.getConsentRevokeRequest(holder.textDocumentUuid.text.toString()).handle(
                    { authRequest ->
                        try {
                            PlanetId.invokeAction(
                                signedDocumentsViewModel.getApplication<DemoRpApplication>(),
                                authRequest!!,
                                "fudosandemorp://${PlanetIdAction.ACTION_CONSENT_REVOKE.action}"
                            )
                        } catch (e: Exception) {
                            signedDocumentsViewModel.handleError(e)
                        }
                    }, { e, _ ->
                        signedDocumentsViewModel.handleError(e)
                    })
            }
        }
    }

    override fun getItemCount() = signedDocuments.size

}