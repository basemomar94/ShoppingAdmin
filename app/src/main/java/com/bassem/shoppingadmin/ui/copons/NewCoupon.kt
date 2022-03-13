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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class NewCoupon : Fragment(R.layout.add_copon) {
    var _binding: AddCoponBinding? = null
    val binding get() = _binding
    var validtyDate: String? = null
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddCoponBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.coponValid.setOnClickListener {
            val dialog = DatePickerDialog(requireContext())
            dialog.setOnDateSetListener(DatePickerDialog.OnDateSetListener { _, Y, M, D ->

                validtyDate = "$D-$M-$Y"
                binding!!.validityDate.text = validtyDate

            })
            dialog.show()
        }
        binding!!.addcoupon.setOnClickListener {
            val idCoupon = UUID.randomUUID().toString()
            println(idCoupon)
            addCopton(getInputs(idCoupon), idCoupon)
        }


    }

    fun getInputs(randomId: String): HashMap<String, Any> {
        val title = binding!!.coponKey.text.toString().lowercase().trim()
        val amount = binding!!.coponAmount.text.toString().trim().toInt()
       // val maxUsers = binding!!.coponMax.text.toString().lowercase().trim().toInt()
       // val expireDate = binding!!.validityDate.text.toString().trim()
        val dataHashMap: HashMap<String, Any> = hashMapOf()
        val ordersList = arrayListOf<String>()
        dataHashMap["title"] = title
        dataHashMap["amount"] = amount
       // dataHashMap["maxUsers"] = maxUsers
       // dataHashMap["expireDate"] = expireDate
        dataHashMap["valid"] = true
        dataHashMap["usingCount"] = 0
        dataHashMap["orders"] = ordersList
        dataHashMap["id"] = randomId
        return dataHashMap

    }

    fun addCopton(data: HashMap<String, Any>, randomId: String) {
        loading()
        db.collection("coupons").document(randomId).set(data).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Coupon is added", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                normal()

            }
        }

    }

    fun loading() {
        binding!!.addcoupon.visibility = View.GONE
        binding!!.progressBar3.visibility = View.VISIBLE
    }

    fun normal() {
        binding!!.addcoupon.visibility = View.VISIBLE
        binding!!.progressBar3.visibility = View.GONE
    }
}