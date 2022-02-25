package com.bassem.shoppingadmin.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.OrderClass
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*
import java.util.logging.Level.parse

class OrdersRecycleAdapter(
    var Orderslist: MutableList<OrderClass>,
    val lisnter: clickInterface
) : RecyclerView.Adapter<OrdersRecycleAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placedDate = itemView.findViewById<TextView>(R.id.dateOrder)
        val status = itemView.findViewById<TextView>(R.id.statusOrder)
        val total = itemView.findViewById<TextView>(R.id.totalOrder)
        val number = itemView.findViewById<TextView>(R.id.numOrder)
        val card = itemView.findViewById<CardView>(R.id.statusCard)

        init {

            itemView.setOnClickListener {
                val position = adapterPosition
                val orderId = Orderslist[position].order_id
                lisnter.click(position, orderId!!)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Orderslist[position]
        val date = item.order_date
        val locale = Locale.US
        val dateFormater = SimpleDateFormat("dd-MM-yyyy", locale)

        holder.placedDate.text = date.toString()
        val status = item.status
        when (status) {
            "pending" -> {
                holder.card.setCardBackgroundColor(Color.YELLOW)
                holder.status.setTextColor(Color.BLACK)
            }
            "confirmed" -> holder.card.setCardBackgroundColor(Color.BLUE)
            "shipped" -> holder.card.setCardBackgroundColor(Color.CYAN)
            "arrived" -> holder.card.setCardBackgroundColor(Color.GREEN)
        }
        holder.status.text = status
        holder.number.text = item.order_id
        holder.total.text = item.cost + " EGP"
    }

    override fun getItemCount(): Int {

        return Orderslist.size
    }

    fun filter(filterList: MutableList<OrderClass>) {
        Orderslist = filterList
        notifyDataSetChanged()

    }


    interface clickInterface {
        fun click(position: Int, order_id: String)
    }
}