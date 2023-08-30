package com.example.gestorednd.Adapters

import android.content.res.Configuration
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Feats
import com.example.gestorednd.R
import org.w3c.dom.Text

class FeatListAdapter(private var featList : ArrayList<Feats>) : RecyclerView.Adapter<FeatListAdapter.MyViewHolder>() {

    var selectedItem : Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.feat_list_item,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = featList[position]
        holder.name.text = currentItem.name
        holder.descr.text = currentItem.description
        //cambio colore per selezione
        val accent : Int
        var normalColor : Int
        if(holder.itemView.context.resources.configuration.uiMode == Configuration.UI_MODE_NIGHT_YES){
            normalColor = ContextCompat.getColor(holder.itemView.context, R.color.dm_primary)
            accent = ContextCompat.getColor(holder.itemView.context, R.color.dm_desat_accent)
        }
        else {
            normalColor = ContextCompat.getColor(holder.itemView.context, R.color.primary)
            accent = ContextCompat.getColor(holder.itemView.context, R.color.desat_accent)
        }

        if(position == selectedItem)
            holder.itemView.setBackgroundColor(accent)
        else
            holder.itemView.setBackgroundColor(normalColor)
        Log.e("boh", "sdahadshdhsa")
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
        featList.removeAt(selectedItem!!)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return featList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.txtFeatName)
        val descr : TextView = itemView.findViewById(R.id.txtFeatDescr)
    }
}