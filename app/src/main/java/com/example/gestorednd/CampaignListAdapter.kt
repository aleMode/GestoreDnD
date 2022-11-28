package com.example.gestorednd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CampaignListAdapter(private val campList : ArrayList<Campaigns>) : RecyclerView.Adapter<CampaignListAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.campaign_list_items,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = campList[position]
        holder.campName.text = currentItem.name
    }

    override fun getItemCount(): Int {
        return campList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val campName : TextView = itemView.findViewById(R.id.txtCampName)

    }
}