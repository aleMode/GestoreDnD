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

        val strVal = view.findViewById<TextView>(R.id.txtStrVal)
            strVal.text = chosen.str.toString()
        strVal.setOnFocusChangeListener { view, b -> //metodo per il salvataggio dei dati
            if(!b){ //controllo per vedere se si è perso il focus
                chosen.str = Integer.parseInt(strVal.text.toString())
            }
        }

        val dexVal = view.findViewById<TextView>(R.id.txtDexVal)
            dexVal.text = chosen.dex.toString()
        dexVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.dex = Integer.parseInt(dexVal.text.toString())
            }
        }

        val conVal = view.findViewById<TextView>(R.id.txtConVal)
            conVal.text = chosen.con.toString()
        conVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.con = Integer.parseInt(conVal.text.toString())
            }
        }

        val intVal = view.findViewById<TextView>(R.id.txtIntVal)
            intVal.text = chosen.int.toString()
        intVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.int = Integer.parseInt(intVal.text.toString())
            }
        }

        val wisVal = view.findViewById<TextView>(R.id.txtWisVal)
            wisVal.text = chosen.wis.toString()
        wisVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.wis = Integer.parseInt(wisVal.text.toString())
            }
        }

        val chaVal = view.findViewById<TextView>(R.id.txtChaVal)
            chaVal.text = chosen.cha.toString()
        strVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.cha = Integer.parseInt(chaVal.text.toString())
            }
        }

        val acrVal = view.findViewById<TextView>(R.id.txtAcrobatics2)
            acrVal.text = chosen.acr.toString()
        acrVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.acr = Integer.parseInt(acrVal.text.toString())
            }
        }

        val athVal = view.findViewById<TextView>(R.id.txtAthletics2)
            athVal.text = chosen.ath.toString()
        athVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.ath = Integer.parseInt(athVal.text.toString())
            }
        }

        val arcVal = view.findViewById<TextView>(R.id.txtArcana2)
            arcVal.text = chosen.arc.toString()
        arcVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.arc = Integer.parseInt(arcVal.text.toString())
            }
        }
        val decVal = view.findViewById<TextView>(R.id.txtDeception2)
            decVal.text = chosen.dec.toString()
        decVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.dec = Integer.parseInt(decVal.text.toString())
            }
        }
        val insVal = view.findViewById<TextView>(R.id.txtInsight2)
            insVal.text = chosen.ins.toString()
        insVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.ins = Integer.parseInt(insVal.text.toString())
            }
        }
        val knoVal = view.findViewById<TextView>(R.id.txtKnowledge2)
            knoVal.text = chosen.kno.toString()
        knoVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.kno = Integer.parseInt(knoVal.text.toString())
            }
        }
        val medVal = view.findViewById<TextView>(R.id.txtMedicine2)
            medVal.text = chosen.med.toString()
        medVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.med = Integer.parseInt(medVal.text.toString())
            }
        }
        val perVal = view.findViewById<TextView>(R.id.txtPerception2)
            perVal.text = chosen.per.toString()
        perVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.per = Integer.parseInt(perVal.text.toString())
            }
        }
        val steVal = view.findViewById<TextView>(R.id.txtStealth2)
            steVal.text = chosen.ste.toString()
        steVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.ste = Integer.parseInt(steVal.text.toString())
            }
        }
        val surVal = view.findViewById<TextView>(R.id.txtSurvival2)
            surVal.text = chosen.sur.toString()
        surVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.sur = Integer.parseInt(surVal.text.toString())
            }
        }
    }
}