package com.example.gestorednd.CharacterSheetFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.Adapters.EquipmentListAdapter
import com.example.gestorednd.Adapters.SpellListAdapter
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.DataClasses.Spells
import com.example.gestorednd.R

class SpellsFragment : Fragment() {

    private lateinit var adapterSpells: SpellListAdapter
    private lateinit var recyclerViewSpells : RecyclerView
    var chosen : Pg = SheetActivity.chosenChar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spells, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inizializzazione della lista delle spells
        val layoutManager = LinearLayoutManager(context)
        recyclerViewSpells = view.findViewById(R.id.lstSpells)
        recyclerViewSpells.layoutManager = layoutManager
        adapterSpells = SpellListAdapter(chosen.spells) //uso dell'adapter ad hoc
        recyclerViewSpells.adapter = adapterSpells

        //bottoni per spells
        val addEquip = view.findViewById<Button>(R.id.btnAddSpell)

        addEquip.setOnClickListener {
            val spellName = view.findViewById<TextView>(R.id.txtNewSpellName)
            val spellLvl = view.findViewById<TextView>(R.id.txtNewSpellLvl)
            SheetActivity.chosenChar.spells.add(Spells(spellName.text.toString(),
                Integer.parseInt(spellLvl.text.toString())!!))
            adapterSpells = SpellListAdapter(chosen.spells)
            recyclerViewSpells.adapter = adapterSpells
            spellName.text = ""
            spellLvl.text = ""
        }

        val removeSpell = view.findViewById<Button>(R.id.btnRemoveSpell)
        removeSpell.setOnClickListener {
            val position = adapterSpells.selectedItem
            if(position != null)
                adapterSpells.deleteItem()
            adapterSpells.selectedItem = null
            adapterSpells = SpellListAdapter(chosen.spells)
        }
    }
}