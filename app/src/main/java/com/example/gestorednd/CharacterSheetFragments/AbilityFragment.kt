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


class AbilityFragment : Fragment() {

    var chosen : Pg = SheetActivity.chosenChar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ability, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.txtStrVal).text = chosen.str.toString()
        view.findViewById<TextView>(R.id.txtDexVal).text = chosen.dex.toString()
        view.findViewById<TextView>(R.id.txtConVal).text = chosen.con.toString()
        view.findViewById<TextView>(R.id.txtIntVal).text = chosen.int.toString()
        view.findViewById<TextView>(R.id.txtWisVal).text = chosen.wis.toString()
        view.findViewById<TextView>(R.id.txtChaVal).text = chosen.cha.toString()

        view.findViewById<TextView>(R.id.txtAcrobatics2).text = chosen.acr.toString()
        view.findViewById<TextView>(R.id.txtAthletics2).text = chosen.ath.toString()
        view.findViewById<TextView>(R.id.txtArcana2).text = chosen.arc.toString()
        view.findViewById<TextView>(R.id.txtDeception2).text = chosen.dec.toString()
        view.findViewById<TextView>(R.id.txtInsight2).text = chosen.ins.toString()
        view.findViewById<TextView>(R.id.txtKnowledge2).text = chosen.kno.toString()
        view.findViewById<TextView>(R.id.txtMedicine2).text = chosen.med.toString()
        view.findViewById<TextView>(R.id.txtPerception2).text = chosen.per.toString()
        view.findViewById<TextView>(R.id.txtStealth2).text = chosen.ste.toString()
        view.findViewById<TextView>(R.id.txtSurvival2).text = chosen.sur.toString()
    }
}