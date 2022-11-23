package com.example.gestorednd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.w3c.dom.Text

class SheetListAdapter(private val charList : ArrayList<com.example.gestorednd.Characters>) : RecyclerView.Adapter<SheetListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sheet_list_items,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = charList[position]
        holder.charName.text = currentItem.name
        holder.charSpec.text = currentItem.specie
        holder.charClass.text = currentItem.clss
        holder.charLvl.text = currentItem.lvl.toString()
    }

    override fun getItemCount(): Int {
        return charList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val charName : TextView = itemView.findViewById(R.id.txtName)
        val charSpec : TextView = itemView.findViewById(R.id.txtSpecie)
        val charClass : TextView = itemView.findViewById(R.id.txtClass)
        val charLvl : TextView = itemView.findViewById(R.id.txtLvl)
    }

}