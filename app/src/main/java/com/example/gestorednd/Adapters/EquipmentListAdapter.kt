package com.example.gestorednd.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Equipment
import com.example.gestorednd.R

class EquipmentListAdapter(private val equipList : ArrayList<Equipment>) : RecyclerView.Adapter<EquipmentListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.equipment_list_items,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = equipList[position]
        holder.item.text = currentItem.item
    }

    override fun getItemCount(): Int {
        return equipList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val item : TextView = itemView.findViewById(R.id.txtElement)

    }


}