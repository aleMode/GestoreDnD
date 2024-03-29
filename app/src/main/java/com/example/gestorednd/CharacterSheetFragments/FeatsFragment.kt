package com.example.gestorednd.CharacterSheetFragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.Adapters.FeatListAdapter
import com.example.gestorednd.Adapters.SpellListAdapter
import com.example.gestorednd.DataClasses.Feats
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.DataClasses.Spells
import com.example.gestorednd.R

class FeatsFragment : Fragment() {

    private lateinit var adapterFeats: FeatListAdapter
    private lateinit var recyclerViewFeats : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inizializzazione della lista dei feats
        val layoutManager = LinearLayoutManager(context)
        recyclerViewFeats = view.findViewById(R.id.lstFeats)
        recyclerViewFeats.layoutManager = layoutManager
        adapterFeats = FeatListAdapter(SheetActivity.chosenChar.feats) //uso dell'adapter ad hoc
        recyclerViewFeats.adapter = adapterFeats

        //bottoni per feats
        val addFeat = view.findViewById<Button>(R.id.btnAddFeat)
        addFeat.setOnClickListener {
            val featName = view.findViewById<TextView>(R.id.txtNewFeatName)
            val featDescr = view.findViewById<TextView>(R.id.txtNewFeatDescr)
            if(featDescr.text.trim().isEmpty()) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Error")
                builder.setMessage("The description is empty")
                builder.setPositiveButton("OK") { dialog, which -> }
                val dialog = builder.create()
                dialog.show()
            }else{
                SheetActivity.chosenChar.feats.add(
                    Feats(featName.text.toString(), featDescr.text.toString())
                )
                featName.text = ""
                featDescr.text = ""
            }
            adapterFeats = FeatListAdapter(SheetActivity.chosenChar.feats)
            recyclerViewFeats.adapter = adapterFeats
        }

        val removeFeat = view.findViewById<Button>(R.id.btnRemoveFeat)
        removeFeat.setOnClickListener {
            val position = adapterFeats.selectedItem
            if(position != null)
                adapterFeats.deleteItem()
            adapterFeats.selectedItem = null
            adapterFeats = FeatListAdapter(SheetActivity.chosenChar.feats)
        }


    }
}