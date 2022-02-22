package com.bassem.shoppingadmin.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bassem.shoppingadmin.R
import com.google.firebase.firestore.FirebaseFirestore

class itemOrders : Fragment(R.layout.item_orders) {
    lateinit var db: FirebaseFirestore
    var item: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    fun getitemOrders() {
        db = FirebaseFirestore.getInstance()
        db.collection("orders").whereArrayContains("items", item!!).get().addOnCompleteListener {

            for (item in it.result!!.documentChanges) {

            }
        }

    }
}