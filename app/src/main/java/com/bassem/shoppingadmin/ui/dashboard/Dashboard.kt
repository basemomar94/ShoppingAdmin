package com.bassem.shoppingadmin.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.databinding.DashboardFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : Fragment(R.layout.dashboard_fragment) {
    var _binding: DashboardFragmentBinding? = null
    val binding get() = _binding
    lateinit var db: FirebaseFirestore
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
        binding!!.usersCard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_users)

        }
        binding!!.orders.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_ordersList)
        }
        binding!!.coponsCard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_coponsFragment)
        }

        newOrders()
        getTotal("items", binding!!.totalItems)
        getTotal("users", binding!!.totalUsers)
        getTotal("coupons", binding!!.totalcoupons)
    }

    fun newOrders() {
        db = FirebaseFirestore.getInstance()
        db.collection("orders").whereEqualTo("status", "pending").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val total = it.result!!.size()
                binding!!.newOrders.text = total.toString()
            }
        }
    }

    fun getTotal(subj: String, textView: TextView) {
        db = FirebaseFirestore.getInstance()
        db.collection(subj).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val total = it.result!!.size()
                textView.text = total.toString()
            }
        }
    }

}