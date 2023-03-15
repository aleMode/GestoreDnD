package com.example.gestorednd.CharacterSheetFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.Adapters.EquipmentListAdapter
import com.example.gestorednd.Adapters.SheetListAdapter
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.R


class EquipmentFragment : Fragment() {

    private lateinit var adapter : EquipmentListAdapter
    private lateinit var recyclerView : RecyclerView
    var chosen : Pg = SheetActivity.chosenChar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.lstEquipped)
        recyclerView.layoutManager = layoutManager
        adapter = EquipmentListAdapter(chosen.equip) //uso dell'adapter ad hoc
        recyclerView.adapter = adapter

        val addEquip = view.findViewById<Button>(R.id.btnAddEquip)
        val removeEquip = view.findViewById<Button>(R.id.btnRemoveEquip)
        val addBag = view.findViewById<Button>(R.id.btnAddBag)
        val removeBag = view.findViewById<Button>(R.id.btnRemoveBag)


    }

}