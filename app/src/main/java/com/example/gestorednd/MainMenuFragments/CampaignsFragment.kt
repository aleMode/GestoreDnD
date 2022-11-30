package com.example.gestorednd.MainMenuFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.CampaignListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CampaignsFragment : Fragment() {

    private lateinit var adapter : CampaignListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var charList : ArrayList<Campaigns>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_campaigns, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.CampaignsView)
        recyclerView.layoutManager = layoutManager
        adapter = CampaignListAdapter(initial())
        recyclerView.adapter = adapter
    }

    private fun initial(): ArrayList<Campaigns> {
        /*charList = arrayListOf<Characters>()

        for (i in 1..100) {
            val pers = Characters("antonio $i", "umano", "fuffa", i)
            charList.add(pers)
        }



        var directory : File = File(".${File.pathSeparator}characters")
        directory.mkdirs()
        val file = File(directory, "characters.txt")*/

        val jsonString = context?.assets?.open("campaigns.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCampaignsType = object : TypeToken<ArrayList<Campaigns>>() {}.type
        var camps : ArrayList<Campaigns> = gson.fromJson(jsonString, listCampaignsType)
        return camps

    }

}