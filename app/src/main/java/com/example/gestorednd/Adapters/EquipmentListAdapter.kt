package com.example.gestorednd.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Equipment
import com.example.gestorednd.DataClasses.Spells
import com.example.gestorednd.R

class EquipmentListAdapter(private val equipList : ArrayList<String>) : RecyclerView.Adapter<EquipmentListAdapter.MyViewHolder>() {

    var selectedItem : Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.equipment_list_items,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = equipList[position]
        holder.item.text = currentItem
        if(position == selectedItem)
            holder.itemView.setBackgroundColor(Color.BLUE)
        else
            holder.itemView.setBackgroundColor(Color.WHITE)
        holder.itemView.setOnClickListener {
            toggleSelection(holder.adapterPosition)

        }
    }

    //Selezione di un item
    fun toggleSelection(position: Int) {
        if (selectedItem == position)
            selectedItem = null
        else
            selectedItem = position
        notifyDataSetChanged()
    }

    //cancellare un item
    fun deleteItem(){
        equipList.removeAt(selectedItem!!)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return equipList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val item : TextView = itemView.findViewById(R.id.txtElement)

    }


}