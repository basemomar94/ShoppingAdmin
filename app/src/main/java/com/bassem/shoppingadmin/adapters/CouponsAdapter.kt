package com.bassem.shoppingadmin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.models.CouponsClass

class CouponsAdapter(
    var couponsList: MutableList<CouponsClass>,
    val listner: onClickinterface
) : RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch = itemView.findViewById<Switch>(R.id.switch1)
        val name = itemView.findViewById<TextView>(R.id.coponName)
        val usersCount = itemView.findViewById<TextView>(R.id.usersCount)
        val delete = itemView.findViewById<ImageView>(R.id.deleteCoupon)

        init {
            delete.setOnClickListener {
                val position = adapterPosition
                val coupon = couponsList[position]
                val item = coupon.id
                println(item + coupon + position)

                listner.deleteCoupon(item!!, coupon, position)

            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.copon_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = couponsList[position]
        holder.name.text = coupon.title
        holder.usersCount.text = "${coupon.amount} discount"
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

    interface onClickinterface {
        fun deleteCoupon(item: String, coupon: CouponsClass, position: Int)


    }
}