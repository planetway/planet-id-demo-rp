package com.planetway.demo.fudosan.ui.accountsettings.signeddocuments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.planetway.demo.R
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory

class SignedDocumentsFragment : Fragment() {

    companion object {
        fun newInstance() = SignedDocumentsFragment()
    }

    private lateinit var viewModel: SignedDocumentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signed_documents, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, AndroidViewModelFactory(activity!!.application)).get(SignedDocumentsViewModel::class.java)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.signedDocumentsList)
        val adapter = SignedDocumentRecyclerViewAdapter(viewModel)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        viewModel.apply {
            signedDocuments.observe(this@SignedDocumentsFragment) {
                adapter.updateList(it)
            }
        }
    }

}