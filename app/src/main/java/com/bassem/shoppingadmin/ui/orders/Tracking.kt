package com.bassem.shoppingadmin.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.OrderedItemsAdapter
import com.bassem.shoppingadmin.databinding.TrackingFragmentBinding
import com.bassem.shoppingadmin.models.OrderedItem
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kofigyan.stateprogressbar.StateProgressBar
import com.squareup.okhttp.*
import org.json.JSONException
import org.json.JSONObject

class Tracking : Fragment(R.layout.tracking_fragment) {
    var _binding: TrackingFragmentBinding? = null
    val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var orderedList: MutableList<OrderedItem>
    lateinit var orderedAdapter: OrderedItemsAdapter
    lateinit var countList: MutableList<String>
    lateinit var db: FirebaseFirestore
    var orderID: String? = null
    var status: String? = null
    lateinit var token: String
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderedList = arrayListOf()
        countList = arrayListOf()
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



        binding!!.trackChip.setOnCheckedChangeListener { group, checkedId ->
            println(checkedId)
            if (checkedId > 0) {
                val chip: Chip = view.findViewById(checkedId)
                status = chip.text.toString()
            }

        }
        recycleSetup()
        gettingItems()
        binding!!.confirm.setOnClickListener {
            updatestatus()
        }


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
                "confirmed" -> {
                    setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                    sub_item()
                }
                "shipped" -> setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                "arrived" -> setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
            }
            sendNotification(name, status)


        }


    }

    fun recycleSetup() {
        recyclerView = binding!!.trackingRV
        orderedAdapter = OrderedItemsAdapter(orderedList, context!!, countList)
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
                val total = it.result!!.get("cost")
                val subtotal = it.result!!.get("subcost")
                val discount = it.result!!.get("discount")
                token = it.result!!.getString("token")!!
                println(discount)
                if (discount == null) {
                    binding!!.discountLayout.visibility = View.GONE
                }
                val shipping = it.result!!.get("delivery")
                val status = it.result!!.get("status")
                name = it.result!!.get("name").toString()
                val address = it.result!!.get("address")
                val phone = it.result!!.get("phone")
                val numberList = it.result!!.get("count")
                try {
                    for (count in numberList as List<String>) {
                        countList.add(count)
                    }
                } catch (E: Exception) {
                    println(E.message)
                }
                binding!!.trackName.text = name.toString()
                binding!!.trackAdress.text = address.toString()
                binding!!.trackPhone.text = phone.toString()
                tracking(status.toString())
                binding!!.total.text = total.toString() + " EGP"
                binding!!.discount.text = "-$discount EGP"
                binding!!.subTotal.text = "$subtotal EGP"
                binding!!.shipping.text = "$shipping EGP"
                val itemsList = it.result!!.get("items")
                if (itemsList != null) {
                    var i = 0
                    for (item in itemsList as List<*>) {
                        db.collection("items").document(item.toString()).get()
                            .addOnSuccessListener {
                                val item = it.toObject(OrderedItem::class.java)
                                if (item != null) {
                                    orderedList.add(item)
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

            }
        }

    }

    fun updatestatus() {
        loading()
        db = FirebaseFirestore.getInstance()
        db.collection("orders").document(orderID!!).update("status", status)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    tracking(status!!)
                    normal()
                    Toast.makeText(context!!, "your order is $status now", Toast.LENGTH_LONG).show()
                    findNavController().navigateUp()
                } else {
                    normal()
                }
            }
    }

    fun loading() {
        binding!!.confirm.visibility = View.GONE
        binding!!.progressBar2.visibility = View.VISIBLE
    }

    fun normal() {
        binding!!.confirm.visibility = View.VISIBLE
        binding!!.progressBar2.visibility = View.GONE
    }

    // to decrease items from the total amount of items
    fun sub_item() {
        db = FirebaseFirestore.getInstance()
        orderedList.zip(countList).forEach { pair ->
            db.collection("items").document(pair.first.id!!)
                .update("amount", FieldValue.increment(-pair.second.toDouble()))

        }
    }

    fun sendNotification(name: String, update: String) {
        val servertoken: String =
            "key=AAAA8wp6gvE:APA91bGkhZC4jPFfmqTiExrbYIi8-hdgqq1W9cC7EC0CMGRUM37o0a36nez9cQI4LKgNQ2Pc1VrBhL9Y04koZsZ97JCXnrctVYmYiI3LUYWZ2egnLHoxgnOGVn2wJmv_Xv0VU2ynnvGN"

        val jsonObject: JSONObject = JSONObject()
        try {
            jsonObject.put("to", token)
            val notification: JSONObject = JSONObject()

            notification.put("title", "Hello $name")
            notification.put("body", "Your order is $update")
            jsonObject.put("notification", notification)
        } catch (e: JSONException) {
            println(e.message)
        }

        val mediaType: MediaType = MediaType.parse("application/json")
        val client: OkHttpClient = OkHttpClient()
        var body: RequestBody = RequestBody.create(mediaType, jsonObject.toString())
        val request: Request? =
            Request.Builder().url("https://fcm.googleapis.com/fcm/send").method("POST", body)
                .addHeader("Authorization", servertoken)
                .addHeader("Content-Type", "application/json").build()
        Thread(Runnable {
            val response: Response = client.newCall(request).execute()
            println("response=========================================${response.message()}")
        }).start()

    }


}
