package com.bassem.shoppingadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.OrderedItem
import com.bumptech.glide.Glide

class OrderedItemsAdapter(
    val orderedList: MutableList<OrderedItem>,
    val context: Context
) : RecyclerView.Adapter<OrderedItemsAdapter.ViewHolder>() {


    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val photo = itemview.findViewById<ImageView>(R.id.orderedPhoto)
        val title = itemview.findViewById<TextView>(R.id.orderedTitle)
        val price=itemview.findViewById<TextView>(R.id.orderedPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ordered_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordered = orderedList[position]
        holder.title.text = ordered.title
        holder.price.text=ordered.price + " EGP"
        val url = ordered.photo
        Glide.with(context).load(url).into(holder.photo)
    }

    override fun getItemCount(): Int {
        return orderedList.size
    }
}