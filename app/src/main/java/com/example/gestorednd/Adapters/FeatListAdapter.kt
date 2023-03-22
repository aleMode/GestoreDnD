package com.example.gestorednd.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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