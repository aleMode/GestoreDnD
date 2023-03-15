package com.example.gestorednd.CharacterSheetFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.R


class EquipmentFragment : Fragment() {

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

        view.findViewById<TextView>(R.id.txtHpV).text = chosen.hp.toString()
        view.findViewById<TextView>(R.id.txtHdV).text = chosen.lvl.toString()
        view.findViewById<TextView>(R.id.txtCaV).text = chosen.ac.toString()
        view.findViewById<TextView>(R.id.txtSpeedV).text = chosen.speed.toString()
        view.findViewById<TextView>(R.id.txtProfV).text = chosen.profBonus.toString()
        view.findViewById<TextView>(R.id.txtInitV).text = chosen.initBonus.toString()
    }

}