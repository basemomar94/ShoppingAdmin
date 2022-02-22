package com.bassem.shoppingadmin.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.ItemsAdapter
import com.bassem.shoppingadmin.databinding.AllItemsFragmentBinding
import com.bassem.shoppingadmin.models.ItemClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class All_items : Fragment(R.layout.all_items_fragment), ItemsAdapter.action {
    var _binding: AllItemsFragmentBinding? = null
    val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var itemsAdapter: ItemsAdapter
    lateinit var itemsList: MutableList<ItemClass>
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemsList = arrayListOf()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AllItemsFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleSetup()
        gettingData()


        binding!!.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_all_items_to_new_item)
            itemsList.clear()
        }
    }

    override fun onDetach() {
        super.onDetach()
        itemsList.clear()
        Thread.interrupted()

    }

    override fun onResume() {
        super.onResume()
        itemsList.clear()
    }

    fun recycleSetup() {
        itemsAdapter = ItemsAdapter(itemsList, this, context!!)
        recyclerView = view!!.findViewById(R.id.all_items_RV)
        recyclerView.apply {
            adapter = itemsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    fun gettingData() {
        db = FirebaseFirestore.getInstance()
        db.collection("items").get().addOnCompleteListener {
            if (it.isSuccessful) {
                Thread(Runnable {
                    var i = 0
                    for (dc: DocumentChange in it.result!!.documentChanges)
                        if (dc.type == DocumentChange.Type.ADDED) {
                            itemsList.add(dc.document.toObject(ItemClass::class.java))

                            activity!!.runOnUiThread {

                                itemsAdapter.notifyDataSetChanged()
                                i++
                                println("$i ==========${itemsList.size}")
                                if (i == itemsList.size) {
                                    stopShimmer()
                                }

                            }

                        }

                }).start()

            }


        }

    }

    override fun delete(position: Int) {
        val id = itemsList[position].id
        db = FirebaseFirestore.getInstance()
        db.collection("items").document(id!!).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                itemsAdapter.notifyItemRemoved(position)
                itemsList.removeAt(position)
            } else {
                Toast.makeText(context!!, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun edit(position: Int) {
        val bundle = Bundle()
        val itemID = itemsList[position].id
        bundle.putString("item", itemID)
        bundle.putBoolean("edit", true)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        navController.navigate(R.id.action_all_items_to_new_item, bundle)
    }

    override fun gotoOrders(position: Int) {
        val bundle = Bundle()
        val itemID = itemsList[position].id
        bundle.putString("item", itemID)
        bundle.putInt("filter", 2)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        navController.navigate(R.id.action_all_items_to_ordersList, bundle)

    }

    fun stopShimmer() {
        binding!!.shimmerLayout.visibility = View.GONE
        binding!!.allItemsRV.visibility = View.VISIBLE
    }
}