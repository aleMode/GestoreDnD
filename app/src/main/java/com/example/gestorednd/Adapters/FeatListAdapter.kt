package com.example.gestorednd.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Feats
import com.example.gestorednd.R

class FeatListAdapter(private var featList : ArrayList<Feats>) : RecyclerView.Adapter<FeatListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.feat_list_item,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = featList[position]
        holder.name.text = currentItem.name
    }

    override fun getItemCount(): Int {
        return featList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.txtFeatName)
    }
}