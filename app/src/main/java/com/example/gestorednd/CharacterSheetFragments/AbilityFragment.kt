package com.example.gestorednd.CharacterSheetFragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.R


class AbilityFragment : Fragment() {

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

        //statistiche e salvataggio quando vengono unfocussate nel vettore
        val strVal = view.findViewById<TextView>(R.id.txtStrVal)
            strVal.text = SheetActivity.chosenChar.str.toString()
        strVal.setOnFocusChangeListener { view, b -> //metodo per il salvataggio dei dati
            if(!b){ //controllo per vedere se si è perso il focus
                SheetActivity.chosenChar.str = Integer.parseInt(strVal.text.toString())
            }
        }

        val dexVal = view.findViewById<TextView>(R.id.txtDexVal)
            dexVal.text = SheetActivity.chosenChar.dex.toString()
        dexVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.dex = Integer.parseInt(dexVal.text.toString())
            }
        }

        val conVal = view.findViewById<TextView>(R.id.txtConVal)
            conVal.text = SheetActivity.chosenChar.con.toString()
        conVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.con = Integer.parseInt(conVal.text.toString())
            }
        }

        val intVal = view.findViewById<TextView>(R.id.txtIntVal)
            intVal.text = SheetActivity.chosenChar.int.toString()
        intVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.int = Integer.parseInt(intVal.text.toString())
            }
        }

        val wisVal = view.findViewById<TextView>(R.id.txtWisVal)
            wisVal.text = SheetActivity.chosenChar.wis.toString()
        wisVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.wis = Integer.parseInt(wisVal.text.toString())
            }
        }

        val chaVal = view.findViewById<TextView>(R.id.txtChaVal)
            chaVal.text = SheetActivity.chosenChar.cha.toString()
        strVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.cha = Integer.parseInt(chaVal.text.toString())
            }
        }

        val acrVal = view.findViewById<TextView>(R.id.txtAcrobatics2)
            acrVal.text = SheetActivity.chosenChar.acr.toString()
        acrVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.acr = Integer.parseInt(acrVal.text.toString())
            }
        }

        val athVal = view.findViewById<TextView>(R.id.txtAthletics2)
            athVal.text = SheetActivity.chosenChar.ath.toString()
        athVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.ath = Integer.parseInt(athVal.text.toString())
            }
        }

        val arcVal = view.findViewById<TextView>(R.id.txtArcana2)
            arcVal.text = SheetActivity.chosenChar.arc.toString()
        arcVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.arc = Integer.parseInt(arcVal.text.toString())
            }
        }
        val decVal = view.findViewById<TextView>(R.id.txtDeception2)
            decVal.text = SheetActivity.chosenChar.dec.toString()
        decVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.dec = Integer.parseInt(decVal.text.toString())
            }
        }
        val insVal = view.findViewById<TextView>(R.id.txtInsight2)
            insVal.text = SheetActivity.chosenChar.ins.toString()
        insVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.ins = Integer.parseInt(insVal.text.toString())
            }
        }
        val knoVal = view.findViewById<TextView>(R.id.txtKnowledge2)
            knoVal.text = SheetActivity.chosenChar.kno.toString()
        knoVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.kno = Integer.parseInt(knoVal.text.toString())
            }
        }
        val medVal = view.findViewById<TextView>(R.id.txtMedicine2)
            medVal.text = SheetActivity.chosenChar.med.toString()
        medVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.med = Integer.parseInt(medVal.text.toString())
            }
        }
        val perVal = view.findViewById<TextView>(R.id.txtPerception2)
            perVal.text = SheetActivity.chosenChar.per.toString()
        perVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.per = Integer.parseInt(perVal.text.toString())
            }
        }
        val steVal = view.findViewById<TextView>(R.id.txtStealth2)
            steVal.text = SheetActivity.chosenChar.ste.toString()
        steVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.ste = Integer.parseInt(steVal.text.toString())
            }
        }
        val surVal = view.findViewById<TextView>(R.id.txtSurvival2)
            surVal.text = SheetActivity.chosenChar.sur.toString()
        surVal.setOnFocusChangeListener { view, b ->
            if(!b){
                SheetActivity.chosenChar.sur = Integer.parseInt(surVal.text.toString())
            }
        }

    }
}