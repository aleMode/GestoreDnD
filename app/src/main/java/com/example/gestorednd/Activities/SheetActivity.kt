package com.example.gestorednd.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.gestorednd.CharacterSheetFragments.*
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.R
import com.example.gestorednd.Interfaces.SheetSwapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Text
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class SheetActivity : AppCompatActivity(), SheetSwapper {

    val fm : FragmentManager = supportFragmentManager
    lateinit var namePgSel : String

    companion object {
        var chosenChar = Pg()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)

        val navFrag = SheetNavFragment()
        fm.beginTransaction().add(R.id.navFragContainer, navFrag).commit()

        val index = intent.getStringExtra("pos")
        chosenChar = initialize(index)

        val statsFrag = StatsFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, statsFrag).commit()

        val saveBtn = findViewById<ImageView>(R.id.logoutMain)
        saveBtn.setOnClickListener{
            save()
        }
    }

    override fun swapStats() {
        val statFrag = StatsFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, statFrag).commit()
    }
    override fun swapAbility() {
        val abilFrag = AbilityFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, abilFrag).commit()
    }
    override fun swapEquip() {
        val equipFrag = EquipmentFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, equipFrag).commit()
    }
    override fun swapSpells() {
        val spellFrag = SpellsFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, spellFrag).commit()
    }
    override fun swapFeats() {
        val featFrag = FeatsFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, featFrag).commit()
    }

    fun initialize(pos : String?) : Pg {
        var index = pos?.toInt()
        var fileName = "characters.json"
        var file = File(filesDir, fileName)
        val jsonString = file.readText()
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
        /*
        val jsonString = assets?.open("characters.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
        */

        namePgSel = chars[index!!].name
        fileName = "$namePgSel.json"
        file = File(filesDir, fileName)
        val jsonString2 = file.readText()
        val gson2 = Gson()
        val chosenPg = object : TypeToken<Pg>() {}.type
        var chosenPg2 : Pg
        try {
            chosenPg2 = gson2.fromJson(jsonString2, chosenPg)
        }catch(e: Exception){
            //se il file è vuoto crea un personaggio nuovo
            chosenPg2 = Pg()
        }
        findViewById<TextView>(R.id.txtCharName).text = namePgSel


        return chosenPg2
    }

    fun save(){
        val gson = Gson()
        val jsonString = gson.toJson(chosenChar)

        //copia del contenuto del file json nello storage interno per poi compiarlo definitivamente
        val fileName : String = "$namePgSel.json"
        val file = File(getFilesDir(), fileName )
        val writer = BufferedWriter(FileWriter(file, false))
        writer.use {
            it.write(jsonString)
            it.newLine()
        }

    }


}