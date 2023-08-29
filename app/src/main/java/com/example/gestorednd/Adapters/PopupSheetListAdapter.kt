package com.example.gestorednd.Adapters

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.R

class PopupSheetListAdapter(private val charList : ArrayList<Characters>) : RecyclerView.Adapter<PopupSheetListAdapter.MyViewHolder>() {

    var selectedItem : Int? = null

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val charName : TextView = itemView.findViewById(R.id.txtName)
        val charSpec : TextView = itemView.findViewById(R.id.txtSpecie)
        val charClass : TextView = itemView.findViewById(R.id.txtClass)
        val charLvl : TextView = itemView.findViewById(R.id.txtLvl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.sheet_list_items,
            parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = charList[position]
        holder.charName.text = currentItem.name
        holder.charSpec.text = currentItem.specie
        holder.charClass.text = currentItem.clss
        holder.charLvl.text = currentItem.lvl.toString()

        //cambio colore per selezione
        if(position == selectedItem)
            holder.itemView.setBackgroundColor(Color.parseColor(R.color.accent.toString()))
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

    fun getSelected() : Characters? {
        if(selectedItem != null)
            return charList[selectedItem!!]
        else
            return null
    }

    override fun getItemCount(): Int {
        return charList.size
    }

}