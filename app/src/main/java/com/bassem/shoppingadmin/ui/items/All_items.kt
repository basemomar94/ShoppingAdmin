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
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class All_items : Fragment(R.layout.all_items_fragment), ItemsAdapter.action,
    SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {
    private var _binding: AllItemsFragmentBinding? = null
    private val binding get() = _binding
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var itemsList: MutableList<ItemClass>
    private lateinit var db: FirebaseFirestore
    private var isChipHidden = true


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
        filter.setOnMenuItemClickListener(this)
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
        if (itemsList.isEmpty()) {
            gettingData()

        } else {
            itemsList.clear()
            gettingData()
        }
        binding!!.chipButtons.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId > -1) {
                when (view.findViewById<Chip>(checkedId).text) {
                    "hidden" -> displayHidenItems()
                    "sold" -> displaySold()
                    "men" -> displayMenWomen("male")
                    "women" -> displayMenWomen("female")


                }
            } else {
                isChipHidden = true
                binding!!.chipButtons.visibility = View.GONE
                itemsAdapter.filter(itemsList)
                itemsAdapter.notifyDataSetChanged()

            }

        }


        binding!!.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_all_items_to_new_item)
        }

    }

    private fun displayMenWomen(category: String) {
        val catogeryList: MutableList<ItemClass>
        catogeryList = arrayListOf()
        itemsList.forEach {
            if (it.category == category) {
                catogeryList.add(it)
            }
        }
        itemsAdapter.filter(catogeryList)

    }


    private fun recycleSetup() {
        itemsAdapter = ItemsAdapter(itemsList, this, requireContext())
        recyclerView = requireView().findViewById(R.id.all_items_RV)
        recyclerView.apply {
            adapter = itemsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun gettingData() {
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

    override fun delete(position: Int, itemId: String, item: ItemClass) {

        db = FirebaseFirestore.getInstance()
        db.collection("items").document(itemId).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                itemsAdapter.notifyItemRemoved(position)
                itemsList.remove(item)
            } else {
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun edit(position: Int, itemId: String) {
        val bundle = Bundle()
        bundle.putString("item", itemId)
        bundle.putBoolean("edit", true)
        val navController =
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
        navController.navigate(R.id.action_all_items_to_new_item, bundle)
    }

    override fun gotoOrders(position: Int, itemId: String) {
        val bundle = Bundle()
        bundle.putString("item", itemId)
        bundle.putInt("filter", 2)
        val navController =
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
        navController.navigate(R.id.action_all_items_to_ordersList, bundle)

    }

    override fun hide(position: Int, itemId: String, item: ItemClass, shown: Boolean) {
        item.visible = !item.visible!!
        if (shown) {
            showOrhide(itemId, false)
        } else {
            showOrhide(itemId, true)
        }
        itemsAdapter.notifyItemChanged(position)
    }

    private fun showOrhide(itemId: String, status: Boolean) {
        db.collection("items").document(itemId).update("visible", status).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "item is updated", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun stopShimmer() {
        binding!!.shimmerLayout.visibility = View.GONE
        binding!!.allItemsLayout.visibility = View.VISIBLE
    }

    private fun searchbyName(filter: String) {
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
        searchbyName(p0.toString().lowercase())
        return true
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        if (isChipHidden) {
            binding!!.chipButtons.visibility = View.VISIBLE
            isChipHidden = false

        } else {
            binding!!.chipButtons.visibility = View.GONE
            isChipHidden = true

        }

        return true
    }

    private fun displayHidenItems() {
        val hiddenList: MutableList<ItemClass>
        hiddenList = arrayListOf()
        itemsList.forEach {
            if (!it.visible!!) {
                hiddenList.add(it)
            }
        }
        itemsAdapter.filter(hiddenList)
    }

    private fun displaySold() {
        val soldList: MutableList<ItemClass>
        soldList = arrayListOf()
        itemsList.forEach {
            if (it.amount!! <= 0) {
                soldList.add(it)
            }
        }
        itemsAdapter.filter(soldList)
    }


}