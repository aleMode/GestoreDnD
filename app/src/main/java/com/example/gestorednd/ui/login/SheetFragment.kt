package com.example.gestorednd.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Characters
import com.example.gestorednd.R
import com.example.gestorednd.SheetListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SheetFragment : Fragment() {

    private lateinit var adapter : SheetListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var charList : ArrayList<Characters>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sheet, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.sheetView)
        recyclerView.layoutManager = layoutManager
        adapter = SheetListAdapter(initial())
        recyclerView.adapter = adapter
    }

    private fun initial(): ArrayList<Characters> {
        /*charList = arrayListOf<Characters>()

        for (i in 1..100) {
            val pers = Characters("antonio $i", "umano", "fuffa", i)
            charList.add(pers)
        }



        var directory : File = File(".${File.pathSeparator}characters")
        directory.mkdirs()
        val file = File(directory, "characters.txt")*/

        val jsonString = context?.assets?.open("characters.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
        return chars

    }

}