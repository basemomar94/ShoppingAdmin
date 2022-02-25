package com.bassem.shoppingadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.ItemClass
import com.bumptech.glide.Glide

class ItemsAdapter(
    var itemsList: MutableList<ItemClass>,
    val lisnter: action,
    val context: Context
) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {


    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val title = itemview.findViewById<TextView>(R.id.itemTitleList)
        val price = itemview.findViewById<TextView>(R.id.priceList)
        val amount = itemview.findViewById<TextView>(R.id.amount)
        val delete = itemview.findViewById<ImageView>(R.id.delete)
        val edit = itemview.findViewById<ImageView>(R.id.edit)
        val photo = itemview.findViewById<ImageView>(R.id.itemPhoto)
        val sold = itemview.findViewById<CardView>(R.id.soldCard)

        init {
            delete.setOnClickListener {
                val position = adapterPosition
                val itemId = itemsList[position].id
                lisnter.delete(position, itemId!!)
            }
            edit.setOnClickListener {
                val position = adapterPosition
                val itemId = itemsList[position].id
                lisnter.edit(position, itemId!!)
            }
            itemview.setOnClickListener {
                val position = adapterPosition
                val itemId = itemsList[position].id
                lisnter.gotoOrders(position, itemId!!)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]
        holder.title.text = item.title
        holder.amount.text = item.amount.toString()
        holder.price.text = "${item.price} EGP"
        if (item.amount!! <= 0) {
            holder.sold.visibility = View.VISIBLE
        } else {
            holder.sold.visibility = View.INVISIBLE

        }

        val url = item.photo
        Glide.with(context).load(url).into(holder.photo)

    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun filter(filterList: MutableList<ItemClass>) {
        itemsList = filterList
        notifyDataSetChanged()
    }

    interface action {
        fun delete(position: Int, itemId: String)
        fun edit(position: Int, itemId: String)
        fun gotoOrders(position: Int, itemId: String)

    }
}