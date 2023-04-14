package com.example.gestorednd.Adapters

import android.content.Intent
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
//Usato nelle singole campagne per mostrare le liste di personaggi usati dai membri
class SheetListAdapterCamp(private val charList : ArrayList<Characters>) : RecyclerView.Adapter<SheetListAdapterCamp.MyViewHolder>() {

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

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SheetActivity::class.java)
            //inserimento dell'indice del personaggio per unpacking
            SheetActivity.campaignChar = true
            intent.putExtra("camp", position.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return charList.size
    }

}