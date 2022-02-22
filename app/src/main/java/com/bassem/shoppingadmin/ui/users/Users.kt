package com.bassem.shoppingadmin.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.adapters.UsersAdapter
import com.bassem.shoppingadmin.databinding.UsersFragmentBinding
import com.bassem.shoppingadmin.models.UserClass
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class Users : Fragment(R.layout.users_fragment), UsersAdapter.usersInterface {
    var _binding: UsersFragmentBinding? = null
    val binding get() = _binding
    lateinit var usersList: MutableList<UserClass>
    lateinit var usersRV: RecyclerView
    lateinit var usersAdapter: UsersAdapter
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersList = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UsersFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleSetup()
        gettingUsers()
    }

    override fun onDetach() {
        super.onDetach()
        usersList.clear()
    }



    fun recycleSetup() {
        usersRV = view!!.findViewById(R.id.usersRv)
        usersAdapter = UsersAdapter(usersList, this)
        usersRV.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    fun gettingUsers() {
        db = FirebaseFirestore.getInstance()
        db.collection("users").get().addOnCompleteListener {
            if (it.isSuccessful) {
                Thread(Runnable {

                    var i = 0
                    println("inside $i")
                    for (dc in it.result!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            usersList.add(dc.document.toObject(UserClass::class.java))
                            activity!!.runOnUiThread {
                                i++
                                usersAdapter.notifyDataSetChanged()
                                if (i == usersList.size) {
                                  stopLoading()

                                }
                            }

                        }
                    }
                }).start()

            }
        }

    }

    override fun orders(position: Int) {
        val id = usersList[position].id
        val bundle = Bundle()
        bundle.putString("user", id)
        bundle.putBoolean("isUser", true)
        val navController = Navigation.findNavController(activity!!, R.id.fragmentContainerView)
        usersList.clear()
        navController.navigate(R.id.action_users_to_ordersList, bundle)
    }

    fun stopLoading(){
        binding!!.usersRv.visibility = View.VISIBLE
        binding!!.usersShimmer.visibility = View.GONE
    }
}