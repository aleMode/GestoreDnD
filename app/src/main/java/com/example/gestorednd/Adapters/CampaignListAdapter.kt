package com.example.gestorednd.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.CampaignActivity
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.R

class CampaignListAdapter(private val campList : ArrayList<Campaigns>) : RecyclerView.Adapter<CampaignListAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.campaign_list_items,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = campList[position]
        holder.campName.text = currentItem.name

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CampaignActivity::class.java)
            //inserimento dell'indice del personaggio per unpacking
            intent.putExtra("pos", position.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return campList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val campName : TextView = itemView.findViewById(R.id.txtCampName)

    }
}