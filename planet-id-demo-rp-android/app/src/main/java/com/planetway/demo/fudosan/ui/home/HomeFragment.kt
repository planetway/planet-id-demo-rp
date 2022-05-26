package com.planetway.demo.fudosan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.planetway.demo.databinding.FragmentHomeBinding
import com.planetway.demo.fudosan.ui.AndroidViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, AndroidViewModelFactory(activity!!.application))
            .get(HomeViewModel::class.java)

        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }
}
