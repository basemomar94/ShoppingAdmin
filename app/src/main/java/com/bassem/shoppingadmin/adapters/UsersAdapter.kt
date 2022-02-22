package com.bassem.shoppingadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.UserClass

class UsersAdapter(
    val usersList: MutableList<UserClass>,
    val listener: usersInterface
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.userName)
        val phone = itemView.findViewById<TextView>(R.id.userPhone)
        val address = itemView.findViewById<TextView>(R.id.userAddress)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.orders(position)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList[position]
        holder.name.text = user.name
        holder.phone.text = user.phone
        holder.address.text = user.address


    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    interface usersInterface {
        fun orders(position: Int)
    }
}