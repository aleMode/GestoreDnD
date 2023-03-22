package com.example.gestorednd.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Spells
import com.example.gestorednd.R

class SpellListAdapter(private var spellList : ArrayList<Spells>) : RecyclerView.Adapter<SpellListAdapter.MyViewHolder>(){

    var selectedItem : Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.spell_list_items,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = spellList[position]
        holder.name.text = currentItem.name
        holder.lvl.text = currentItem.lvl.toString()
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
        spellList.removeAt(selectedItem!!)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return spellList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.txtSpellName)
        val lvl : TextView =  itemView.findViewById(R.id.txtSpellLvl)
    }
}