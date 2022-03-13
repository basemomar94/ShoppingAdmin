package com.bassem.shoppingadmin.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.LoginActivity
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.databinding.DashboardFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : Fragment(R.layout.dashboard_fragment) {
    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()

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
        binding!!.logOut.setOnClickListener {
            logOut()
        }

        newOrders()
        getTotal("items", binding!!.totalItems)
        getTotal("users", binding!!.totalUsers)
        getTotal("coupons", binding!!.totalcoupons)
    }

    private fun newOrders() {
        db.collection("orders").whereEqualTo("status", "pending").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val total = it.result!!.size()
                binding!!.newOrders.text = total.toString()
            }
        }
    }

    private fun getTotal(subj: String, textView: TextView) {
        db.collection(subj).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val total = it.result!!.size()
                textView.text = total.toString()
            }
        }
    }

    private fun logOut() {
        binding!!.logOut.visibility = View.INVISIBLE
        binding!!.progressBar4.visibility = View.VISIBLE
        val sharedPreferences =
            requireActivity().getSharedPreferences("log", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("log", false)
        editor.apply()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

}