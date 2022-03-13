package com.bassem.shoppingadmin.ui.copons

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.databinding.AddCoponBinding
import com.bassem.shoppingadmin.ui.items.New_item
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class NewCoupon : Fragment(R.layout.add_copon) {
    private var _binding: AddCoponBinding? = null
    private val binding get() = _binding
    private var validityDate: String? = null
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCoponBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.coponValid.setOnClickListener {
            val dialog = DatePickerDialog(requireContext())
            dialog.setOnDateSetListener { _, Y, M, D ->

                validityDate = "$D-$M-$Y"
                binding!!.validityDate.text = validityDate

            }
            dialog.show()
        }
        binding!!.addcoupon.setOnClickListener {
            val idCoupon = UUID.randomUUID().toString()
            println(idCoupon)
            addCoupons(getInputs(idCoupon), idCoupon)
        }


    }

    private fun getInputs(randomId: String): HashMap<String, Any> {

        val title = binding!!.coponKey.text.toString().lowercase().trim()
        val amount = binding!!.coponAmount.text.toString().trim()
        // val maxUsers = binding!!.coponMax.text.toString().lowercase().trim().toInt()
        // val expireDate = binding!!.validityDate.text.toString().trim()
        New_item().errorEmpty(title, binding!!.couponTlayout)
        New_item().errorEmpty(amount, binding!!.couponVlayout)
        val dataHashMap: HashMap<String, Any> = hashMapOf()
        val ordersList = arrayListOf<String>()
        dataHashMap["title"] = title
        if (amount.isNotEmpty()){
            dataHashMap["amount"] = amount.toInt()

        }
        // dataHashMap["maxUsers"] = maxUsers
        // dataHashMap["expireDate"] = expireDate
        dataHashMap["valid"] = true
        dataHashMap["usingCount"] = 0
        dataHashMap["orders"] = ordersList
        dataHashMap["id"] = randomId
        return dataHashMap

    }

    private fun addCoupons(data: HashMap<String, Any>, randomId: String) {
        loading()
        if (binding!!.coponKey.text!!.isNotEmpty() && binding!!.coponAmount.text!!.isNotEmpty()) {
            db.collection("coupons").document(randomId).set(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Coupon is added", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    normal()
                    Toast.makeText(
                        requireContext(),
                        it.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        } else {
            normal()
        }


    }

    private fun loading() {
        binding!!.addcoupon.visibility = View.GONE
        binding!!.progressBar3.visibility = View.VISIBLE
    }

    private fun normal() {
        binding!!.addcoupon.visibility = View.VISIBLE
        binding!!.progressBar3.visibility = View.GONE
    }
}