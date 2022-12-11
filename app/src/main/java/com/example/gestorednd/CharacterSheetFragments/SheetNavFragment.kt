package com.example.gestorednd.CharacterSheetFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.R
import com.example.gestorednd.Interfaces.SheetSwapper


class SheetNavFragment : Fragment() {

    private lateinit var swapper : SheetSwapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        swapper = activity as SheetSwapper
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sheet_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stats = view.findViewById<Button>(R.id.btnStat)
        val ability = view.findViewById<Button>(R.id.btnAbilities)
        val equip = view.findViewById<Button>(R.id.btnEquip)
        val spells = view.findViewById<Button>(R.id.btnSpells)
        val other = view.findViewById<Button>(R.id.btnOther)

        val sheetFrag = SheetActivity()



        stats.setOnClickListener {
            swapper.swapStats()
        }
        ability.setOnClickListener {
            swapper.swapAbility()
        }
        equip.setOnClickListener {
            swapper.swapEquip()
        }
        spells.setOnClickListener {
            swapper.swapSpells()
        }
        other.setOnClickListener {
            swapper.swapFeats()
        }
    }

}