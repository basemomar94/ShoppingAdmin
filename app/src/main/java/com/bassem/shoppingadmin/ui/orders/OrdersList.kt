package com.bassem.shoppingadmin.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.OrdersRecycleAdapter
import com.bassem.shoppingadmin.databinding.OrdersFragmentBinding
import com.bassem.shoppingadmin.models.OrderClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrdersList : Fragment(R.layout.orders_fragment), OrdersRecycleAdapter.clickInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var orderAdapter: OrdersRecycleAdapter
    lateinit var orderList: MutableList<OrderClass>
    var _binding: OrdersFragmentBinding? = null
    val binding get() = _binding
    lateinit var db: FirebaseFirestore
    var filterOrders = 0
    var userId: String? = null
    var itemId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderList = arrayListOf()
        val bundle = this.arguments
        if (bundle != null) {
            filterOrders = bundle.getInt("filter")
            userId = bundle.getString("user")
            itemId = bundle.getString("item")

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OrdersFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDetach() {
        super.onDetach()
        orderList.clear()

    }

    override fun onPause() {
        super.onPause()
        orderList.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleSetup()
        when (filterOrders) {
            0 -> getOrders()
            1 -> getSingleUserOrders(userId!!)
            2 -> getSingleItemOrders(itemId!!)
        }


    }


    fun recycleSetup() {
        orderAdapter = OrdersRecycleAdapter(orderList, this)
        recyclerView = view!!.findViewById(R.id.ordersRV)
        recyclerView.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)

        }

    }

    override fun click(position: Int) {
        val order = orderList[position].order_id
        val bundle = Bundle()
        bundle.putString("order", order)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        navController.navigate(R.id.action_ordersList_to_tracking, bundle)

    }

    fun getOrders() {
        db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .orderBy("order_date", Query.Direction.DESCENDING).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    Thread(Runnable {
                        var i = 0
                        for (dc in it.result!!.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                orderList.add(dc.document.toObject(OrderClass::class.java))
                                activity!!.runOnUiThread {
                                    i++
                                    orderAdapter.notifyDataSetChanged()
                                    if (i == orderList.size) {
                                        binding!!.ordersRV.visibility = View.VISIBLE
                                        binding!!.loadingSpinner4.visibility = View.GONE
                                    }
                                }

                            }
                        }
                    }).start()


                }
            }


    }

    private fun getSingleUserOrders(user: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .orderBy("order_date", Query.Direction.DESCENDING).whereEqualTo("user_id", user).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Thread(Runnable {
                        var i = 0
                        for (dc in it.result!!.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                orderList.add(dc.document.toObject(OrderClass::class.java))
                                activity!!.runOnUiThread {
                                    i++
                                    orderAdapter.notifyDataSetChanged()
                                    if (i == orderList.size) {
                                        binding!!.ordersRV.visibility = View.VISIBLE
                                        binding!!.loadingSpinner4.visibility = View.GONE
                                    }
                                }

                            }
                        }
                    }).start()


                }
            }
    }

    fun getSingleItemOrders(item: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .orderBy("order_date", Query.Direction.DESCENDING).whereArrayContains("items", item!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Thread(Runnable {
                        var i = 0
                        for (dc in it.result!!.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                orderList.add(dc.document.toObject(OrderClass::class.java))
                                activity!!.runOnUiThread {
                                    i++
                                    orderAdapter.notifyDataSetChanged()
                                    if (i == orderList.size) {
                                        binding!!.ordersRV.visibility = View.VISIBLE
                                        binding!!.loadingSpinner4.visibility = View.GONE
                                    }
                                }

                            }
                        }
                    }).start()


                }
            }
    }

}