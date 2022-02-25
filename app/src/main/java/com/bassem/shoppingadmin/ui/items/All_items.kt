package com.bassem.shoppingadmin.ui.items

import android.os.Bundle
import android.view.*
import android.widget.SearchView
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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class All_items : Fragment(R.layout.all_items_fragment), ItemsAdapter.action,
    SearchView.OnQueryTextListener {
    var _binding: AllItemsFragmentBinding? = null
    val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var itemsAdapter: ItemsAdapter
    lateinit var itemsList: MutableList<ItemClass>
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        itemsList = arrayListOf()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_filter, menu)
        val search = menu.findItem(R.id.app_bar_search)
        val filter = menu.findItem(R.id.appbarFilter)
        val SearchView = search.actionView as SearchView
        SearchView.setOnQueryTextListener(this)
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
                var i = 0
                for (dc: DocumentChange in it.result!!.documentChanges)
                    if (dc.type == DocumentChange.Type.ADDED) {
                        itemsList.add(dc.document.toObject(ItemClass::class.java))

                        itemsAdapter.notifyDataSetChanged()
                        i++
                        println("$i ==========${itemsList.size}")
                        if (i == itemsList.size) {
                            stopShimmer()
                        }
                    }
            }
        }

    }

    override fun delete(position: Int, itemId: String) {

        db = FirebaseFirestore.getInstance()
        db.collection("items").document(itemId).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                itemsAdapter.notifyItemRemoved(position)
                itemsList.removeAt(position)
            } else {
                Toast.makeText(context!!, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun edit(position: Int, itemId: String) {
        val bundle = Bundle()
        bundle.putString("item", itemId)
        bundle.putBoolean("edit", true)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        navController.navigate(R.id.action_all_items_to_new_item, bundle)
    }

    override fun gotoOrders(position: Int, itemId: String) {
        val bundle = Bundle()
        bundle.putString("item", itemId)
        bundle.putInt("filter", 2)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        navController.navigate(R.id.action_all_items_to_ordersList, bundle)

    }

    fun stopShimmer() {
        binding!!.shimmerLayout.visibility = View.GONE
        binding!!.allItemsLayout.visibility = View.VISIBLE
    }

    fun filterItems(filter: String) {

        val filterList: MutableList<ItemClass> = arrayListOf()
        itemsList.forEach {
            println("$filter ============== ${it.title}")
            if (it.title!!.lowercase().contains(filter) || it.id!!.lowercase().contains(filter)) {
                filterList.add(it)
                println("${it.title} CONTAIN")
            }
            itemsAdapter.filter(filterList)
        }

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        filterItems(p0.toString().lowercase())
        return true
    }

}