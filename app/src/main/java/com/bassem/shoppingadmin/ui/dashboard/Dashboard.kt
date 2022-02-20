package com.bassem.shoppingadmin.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.databinding.DashboardFragmentBinding

class Dashboard : Fragment(R.layout.dashboard_fragment) {
    var _binding: DashboardFragmentBinding? = null
    val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.allItemsCard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_all_items)
        }
    }

}