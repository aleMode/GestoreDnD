package com.example.gestorednd.MainMenuFragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.R
import com.example.gestorednd.Adapters.SheetListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileNotFoundException

class SheetFragment : Fragment() {

    private lateinit var adapter : SheetListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var charList : ArrayList<Characters>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sheet, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.sheetView)
        recyclerView.layoutManager = layoutManager
        adapter = SheetListAdapter(initial()) //uso dell'adapter ad hoc
        recyclerView.adapter = adapter

        val btnNewChar = view.findViewById<Button>(R.id.btnNewChar)
        btnNewChar.setOnClickListener { //popup per inserire roba del nuovo personaggio
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.popup)
            val nameP = dialog.findViewById<EditText>(R.id.txtCharNamePop)
            val specP = dialog.findViewById<EditText>(R.id.txtCharSpecPop)
            val classP = dialog.findViewById<EditText>(R.id.txtCharClassPop)
            val lvlP = dialog.findViewById<EditText>(R.id.txtCharLvlPop)
            val buttonAdd = dialog.findViewById<Button>(R.id.btnAddPopup)
            //creazione del personaggio alla conferma
            buttonAdd.setOnClickListener {
                //aggiunta del nuovo personaggio alla lista
                if(
                    !nameP.text.toString().isEmpty()
                    || !specP.text.toString().isEmpty()
                    || !classP.text.toString().isEmpty()
                    || Integer.parseInt(lvlP.text.toString()) != null
                ) {
                    val char = Characters(
                        nameP.text.toString(),
                        specP.text.toString(),
                        classP.text.toString(),
                        Integer.parseInt(lvlP.text.toString())
                    )
                    //creazione file del nuovo personaggio
                    val filename = nameP.text.toString() + ".json"
                    val file = File(context?.filesDir, filename)
                    if(file.exists()){
                        dialog.findViewById<TextView>(R.id.txtPopErr).text = "Error"
                    }else {
                        charList.add(char)
                        adapter = SheetListAdapter(charList)
                        recyclerView.adapter = adapter

                        file.createNewFile()
                    }


                }else{
                    dialog.findViewById<TextView>(R.id.txtPopErr).text = "Error"
                }

                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun initial(): ArrayList<Characters> {
        val filename = "characters.json"
        val file = File(context?.filesDir, filename)
        var chars : ArrayList<Characters> = arrayListOf()
        try {
            val jsonString = file.readText()
            val gson = Gson()
            val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
            chars = gson.fromJson(jsonString, listCharactersType)

        } catch (e: FileNotFoundException) {
            //se il file non esiste crealo
            file.createNewFile()
        } catch (e: Exception) {
            Log.e("FileUtils", "Error ")
        }

        charList = chars //per refresh dell'adapter

        //caricamento da asset
        /*val jsonString = context?.assets?.open("characters.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)*/

        return chars

    }

}