package com.bassem.shoppingadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.UserClass

class UsersAdapter(
    val usersList: MutableList<UserClass>
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.userName)
        val phone = itemView.findViewById<TextView>(R.id.userPhone)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList[position]
        holder.name.text = user.name
        holder.phone.text = user.phone
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}