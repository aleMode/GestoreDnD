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

    private lateinit var adapterEquip : EquipmentListAdapter
    private lateinit var adapterBag : EquipmentListAdapter
    private lateinit var recyclerViewEquip : RecyclerView
    private lateinit var recyclerViewBag : RecyclerView
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

        //inizializzazione della lista degli elementi equipaggiati
        val layoutManager = LinearLayoutManager(context)
        recyclerViewEquip = view.findViewById(R.id.lstEquipped)
        recyclerViewEquip.layoutManager = layoutManager
        adapterEquip = EquipmentListAdapter(chosen.equip) //uso dell'adapter ad hoc
        recyclerViewEquip.adapter = adapterEquip

        //iniializzazione della lista degli elementi nello zaino
        val layoutManager2 = LinearLayoutManager(context)
        recyclerViewBag = view.findViewById(R.id.lstBag)
        recyclerViewBag.layoutManager = layoutManager2
        adapterBag = EquipmentListAdapter(chosen.bag) //uso dell'adapter ad hoc
        recyclerViewBag.adapter = adapterBag

        //bottoni per equip
        val addEquip = view.findViewById<Button>(R.id.btnAddEquip)
        addEquip.setOnClickListener {
            val itemName = view.findViewById<TextView>(R.id.txtItemEquip)
            SheetActivity.chosenChar.equip.add(itemName.text.toString())
            adapterEquip = EquipmentListAdapter(chosen.equip)
            recyclerViewEquip.adapter = adapterEquip
            itemName.text=""
        }
        val removeEquip = view.findViewById<Button>(R.id.btnRemoveEquip)
        removeEquip.setOnClickListener {
            adapterEquip = EquipmentListAdapter(chosen.equip)
            val position = adapterEquip.selectedItem
            if(position != null)
                adapterEquip.deleteItem()
        }

        //bottoni per zaino
        val addBag = view.findViewById<Button>(R.id.btnAddBag)
        addBag.setOnClickListener {
            val itemName =  view.findViewById<TextView>(R.id.txtItemBag)
            SheetActivity.chosenChar.bag.add(itemName.text.toString())
            adapterBag = EquipmentListAdapter(chosen.bag)
            recyclerViewBag.adapter = adapterBag
            itemName.text = ""
        }
        val removeBag = view.findViewById<Button>(R.id.btnRemoveBag)
        removeBag.setOnClickListener {
            adapterBag = EquipmentListAdapter(chosen.bag)
            val position = adapterBag.selectedItem
            if(position != null)
                adapterBag.deleteItem()
        }
    }

}