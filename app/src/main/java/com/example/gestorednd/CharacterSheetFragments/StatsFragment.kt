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
import org.w3c.dom.Text

class StatsFragment() : Fragment() {

    var chosen : Pg = SheetActivity.chosenChar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hpVal = view.findViewById<TextView>(R.id.txtHpV)
            hpVal.text = chosen.hp.toString()
        hpVal.setOnFocusChangeListener { view, b -> //aggiorna i valori dopo il cambiamento
            if(!b){
                chosen.hp = Integer.parseInt(hpVal.text.toString())
            }
        }

        val hdVal = view.findViewById<TextView>(R.id.txtHdV) //la vita segue i livelli, non editabile
            hdVal.text = chosen.lvl.toString()

        val caVal = view.findViewById<TextView>(R.id.txtCaV)
            caVal.text = chosen.ac.toString()
        caVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.ac = Integer.parseInt(caVal.text.toString())
            }
        }

        val speedVal = view.findViewById<TextView>(R.id.txtSpeedV)
            speedVal.text = chosen.speed.toString()
        speedVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.speed = Integer.parseInt(speedVal.text.toString())
            }
        }

        val profVal = view.findViewById<TextView>(R.id.txtProfV)
            profVal.text = chosen.profBonus.toString()
        profVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.profBonus = Integer.parseInt(profVal.text.toString())
            }
        }

        val initVal = view.findViewById<TextView>(R.id.txtInitV)
            initVal.text = chosen.initBonus.toString()
        initVal.setOnFocusChangeListener { view, b ->
            if(!b){
                chosen.initBonus = Integer.parseInt(initVal.text.toString())
            }
        }

    }

}