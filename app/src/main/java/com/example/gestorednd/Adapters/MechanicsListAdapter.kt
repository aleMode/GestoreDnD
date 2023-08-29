package com.example.gestorednd.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Mechanics
import com.example.gestorednd.R
import org.w3c.dom.Text

class MechanicsListAdapter(private var mechList : ArrayList<Mechanics>): RecyclerView.Adapter<MechanicsListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.txtMechName)
        val descr = itemView.findViewById<TextView>(R.id.txtMechDescr)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MechanicsListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.mechanic_list_item,
            parent, false)
        return MechanicsListAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MechanicsListAdapter.MyViewHolder, position: Int) {
        val currentItem = mechList[position]
        holder.name.text = currentItem.name
        holder.descr.text = currentItem.description
        holder.descr.visibility == View.GONE

        holder.itemView.setOnClickListener {
            if(holder.descr.visibility == View.GONE)
                holder.descr.visibility == View.VISIBLE
            else
                holder.descr.visibility == View.GONE

            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return mechList.size
    }
}