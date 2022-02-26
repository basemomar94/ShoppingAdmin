package com.bassem.shoppingadmin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.CouponsClass
import kotlinx.android.synthetic.main.item.view.*

class CouponsAdapter(
    var couponsList: MutableList<CouponsClass>
) : RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch = itemView.findViewById<Switch>(R.id.switch1)
        val name = itemView.findViewById<TextView>(R.id.coponName)
        val usersCount = itemView.findViewById<TextView>(R.id.usersCount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.copon_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = couponsList[position]
        holder.name.text = coupon.title
        holder.usersCount.text = coupon.usingCount.toString()
     if (coupon.valid!!) {
            holder.switch.isChecked = true
            holder.switch.text = "Active"
        } else {
            holder.switch.isChecked = false
            holder.switch.text = "inactive"
        }

    }

    override fun getItemCount(): Int {
        return couponsList.size
    }
}