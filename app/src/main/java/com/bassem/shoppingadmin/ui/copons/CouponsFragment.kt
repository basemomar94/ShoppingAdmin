package com.bassem.shoppingadmin.ui.copons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.CouponsAdapter
import com.bassem.shoppingadmin.databinding.CoponsFragmentBinding
import com.bassem.shoppingadmin.models.CouponsClass
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class CouponsFragment : Fragment(R.layout.copons_fragment) {
    var _binding: CoponsFragmentBinding? = null
    val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var couponsAdapter: CouponsAdapter
    var couponsList: MutableList<CouponsClass> = arrayListOf()
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CoponsFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.addnewCopon.setOnClickListener {
            findNavController().navigate(R.id.action_coponsFragment_to_newCopon2)
            val valid = couponsList[0].valid
            println(valid)
        }
        recycleviewSetup()
        gettingCoupons()

    }

    fun recycleviewSetup() {
        recyclerView = binding!!.coyponsRv
        couponsAdapter = CouponsAdapter(couponsList)
        recyclerView.apply {
            adapter = couponsAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context!!)
        }
    }

    fun gettingCoupons() {
        db = FirebaseFirestore.getInstance()
        db.collection("coupons").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result
                for (dc in result!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        couponsList.add(dc.document.toObject(CouponsClass::class.java))
                        couponsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}