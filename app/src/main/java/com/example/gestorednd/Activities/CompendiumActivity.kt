package com.example.gestorednd.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.FeatListAdapter
import com.example.gestorednd.Adapters.MechanicsListAdapter
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Mechanics
import com.example.gestorednd.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class CompendiumActivity : AppCompatActivity() {

    private lateinit var adapterMechs: MechanicsListAdapter
    private lateinit var recyclerViewMech : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compendium)

        var mechList : ArrayList<Mechanics> = arrayListOf()
        //aggiornamento della lista delle meccaniche
        val storageF = Firebase.firestore

        val jsonString = this?.assets?.open("diceRolls.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Mechanics>>() {}.type
        mechList = gson.fromJson(jsonString, listCharactersType)

        //inizializzazione della lista delle meccaniche
        val layoutManager = LinearLayoutManager(this)
        recyclerViewMech = findViewById(R.id.lst_mechanics)
        recyclerViewMech.layoutManager = layoutManager
        adapterMechs = MechanicsListAdapter(mechList)
        recyclerViewMech.adapter = adapterMechs
    }

    private fun toMechList(data: Map<String, Any>?): ArrayList<Mechanics> {
        val rawMechanics = data?.get("rolls") as ArrayList<Pair<String, String>>? ?: arrayListOf()

        Log.e("CompendiumActivity", "$rawMechanics")

        var mechanicsArray = rawMechanics.map{
            Mechanics(it.first, it.second)
        } as ArrayList<Mechanics>

        return mechanicsArray
    }


}