package com.bassem.shoppingadmin.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.OrderedItemsAdapter
import com.bassem.shoppingadmin.databinding.TrackingFragmentBinding
import com.bassem.shoppingadmin.models.OrderedItem
import com.google.firebase.firestore.FirebaseFirestore
import com.kofigyan.stateprogressbar.StateProgressBar

class Tracking : Fragment(R.layout.tracking_fragment) {
    var _binding: TrackingFragmentBinding? = null
    val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var orderedList: MutableList<OrderedItem>
    lateinit var orderedAdapter: OrderedItemsAdapter
    lateinit var db: FirebaseFirestore
    var orderID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderedList = arrayListOf()
        val bundle = this.arguments
        if (bundle != null) {
            orderID = bundle.getString("order")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TrackingFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleSetup()
        gettingItems()


    }

    fun tracking(status: String) {
        val track = binding!!.trackingBar
        track.apply {
            stateDescriptionData.add(0, "Pending")
            stateDescriptionData.add(1, "Confirmed")
            stateDescriptionData.add(2, "Shipped")
            stateDescriptionData.add(3, "Arrived")
            when (status) {
                "pending" -> setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
                "confirmed" -> setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                "shipped" -> setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                "arrived" -> setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
            }


        }


    }

    fun recycleSetup() {
        recyclerView = binding!!.trackingRV
        orderedAdapter = OrderedItemsAdapter(orderedList, context!!)
        recyclerView.apply {
            adapter = orderedAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    fun gettingItems() {
        db = FirebaseFirestore.getInstance()
        db.collection("orders").document(orderID!!).get().addOnCompleteListener {
            if (it.isSuccessful) {
                Thread(Runnable {
                    val total = it.result!!.get("cost")
                    val status = it.result!!.get("status")
                    val name = it.result!!.get("name")
                    val address = it.result!!.get("address")
                    val phone = it.result!!.get("phone")
                    binding!!.trackName.text = name.toString()
                    binding!!.trackAdress.text = address.toString()
                    binding!!.trackPhone.text = phone.toString()
                    tracking(status.toString())
                    binding!!.total.text = total.toString() + " EGP"
                    binding!!.subTotal.text = (total.toString().toInt() - 10).toString() + " EGP"
                    val itemsList = it.result!!.get("items")
                    if (itemsList != null) {
                        var i = 0
                        for (item in itemsList as List<*>) {
                            db.collection("items").document(item.toString()).get()
                                .addOnSuccessListener {
                                    val item = it.toObject(OrderedItem::class.java)
                                    orderedList.add(item!!)
                                    activity!!.runOnUiThread {
                                        i++
                                        orderedAdapter.notifyDataSetChanged()
                                        if (i == (itemsList as List<*>).size) {
                                            binding!!.trackLayout.visibility = View.VISIBLE
                                            binding!!.loadingSpinner3.visibility = View.GONE
                                        }
                                    }


                                }


                        }
                    }

                }).start()

            }
        }

    }


}
