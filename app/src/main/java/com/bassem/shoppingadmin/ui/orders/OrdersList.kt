package com.bassem.shoppingadmin.ui.orders

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.OrdersRecycleAdapter
import com.bassem.shoppingadmin.databinding.OrdersFragmentBinding
import com.bassem.shoppingadmin.models.OrderClass
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrdersList : Fragment(R.layout.orders_fragment), OrdersRecycleAdapter.clickInterface,
    SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var orderAdapter: OrdersRecycleAdapter
    lateinit var orderList: MutableList<OrderClass>
    var _binding: OrdersFragmentBinding? = null
    val binding get() = _binding
    lateinit var db: FirebaseFirestore
    var filterOrders = 0
    var userId: String? = null
    var itemId: String? = null
    var isHidden = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        orderList = arrayListOf()
        val bundle = this.arguments
        if (bundle != null) {
            filterOrders = bundle.getInt("filter")
            userId = bundle.getString("user")
            itemId = bundle.getString("item")

        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_filter, menu)
        val search = menu.findItem(R.id.app_bar_search)
        val filter = menu.findItem(R.id.appbarFilter)
        val SearchView = search.actionView as SearchView
        SearchView.setOnQueryTextListener(this)
        filter.setOnMenuItemClickListener(this)
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
        // orderList.clear()
    }

    override fun onResume() {
        super.onResume()
        normal()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleSetup()
        println("$filterOrders========================================")
        when (filterOrders) {
            0 -> getOrders()
            1 -> getSingleUserOrders(userId!!)
            2 -> getSingleItemOrders(itemId!!)
            else -> getOrders()
        }
        binding!!.showFilter.setOnClickListener {
            if (isHidden) {
                binding!!.chipButtons.visibility = View.VISIBLE
                isHidden = false

            } else {
                binding!!.chipButtons.visibility = View.GONE
                isHidden = true

            }
        }
        binding!!.chipButtons.setOnCheckedChangeListener { group, checkedId ->
            println(checkedId)
            if (checkedId > 0) {
                val selectedChip: Chip = view.findViewById(checkedId)

                val filterStatus = resources.getResourceEntryName(checkedId)
                when (filterStatus) {
                    "pending" -> filterOrders(filterStatus, selectedChip)
                    "confirmed" -> filterOrders(filterStatus, selectedChip)
                    "shipped" -> filterOrders(filterStatus, selectedChip)
                    "arrived" -> filterOrders(filterStatus, selectedChip)
                    "canceled" -> filterOrders(filterStatus, selectedChip)
                }
            } else {
                binding!!.chipButtons.visibility = View.GONE
                isHidden = true
                orderAdapter.filter(orderList)
                orderAdapter.notifyDataSetChanged()
            }


        }
        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filterId = p0.toString().lowercase()
                filterOrders(filterId, binding!!.arrived)
                binding!!.chipButtons.visibility = View.GONE
                return true
            }
        })


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

    override fun click(position: Int, order_id: String) {

        //  val order = orderList[position].order_id
        val bundle = Bundle()
        bundle.putString("order", order_id)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        navController.navigate(R.id.action_ordersList_to_tracking, bundle)

    }

    fun getOrders() {
        println("Inside fun")
        db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .orderBy("order_date", Query.Direction.DESCENDING).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    var i = 0
                    for (dc in it.result!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            orderList.add(dc.document.toObject(OrderClass::class.java))
                            i++
                            orderAdapter.notifyDataSetChanged()
                            if (i == orderList.size) {
                                normal()
                            }

                        }
                    }

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
                                        normal()
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
                                        normal()
                                    }
                                }

                            }
                        }
                    }).start()


                }
            }
    }

    fun normal() {
        binding!!.ordersLayout.visibility = View.VISIBLE
        binding!!.loadingSpinner4.visibility = View.GONE

    }

    fun filterOrders(filter: String, chip: Chip) {
        val filterList: MutableList<OrderClass> = arrayListOf()
        for (order in orderList) {
            if (order.status == filter || order.order_id!!.lowercase().contains(filter)) {
                filterList.add(order)

            }
        }
        val count = filterList.size
        chip.text = "$filter ($count)"
        orderAdapter.filter(filterList)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        val filterId = p0.toString().lowercase()
        filterOrders(filterId, binding!!.arrived)
        binding!!.chipButtons.visibility = View.GONE
        return true
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        if (isHidden) {
            binding!!.chipButtons.visibility = View.VISIBLE
            isHidden = false

        } else {
            binding!!.chipButtons.visibility = View.GONE
            isHidden = true

        }

        return true
    }

}