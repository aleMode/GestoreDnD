package com.example.gestorednd.Activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.gestorednd.CharacterSheetFragments.*
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.R
import com.example.gestorednd.Interfaces.SheetSwapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class SheetActivity : AppCompatActivity(), SheetSwapper {

    val fm : FragmentManager = supportFragmentManager

    companion object {
        var chosenChar = Pg()
        var campaignChar = false

        //personaggio correntemente visualizzato / da visalizzare
        var namePgSel : String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)

        val navFrag = SheetNavFragment()
        fm.beginTransaction().add(R.id.navFragContainer, navFrag).commit()

        //ricezione della posizione del personaggio da selezionare
        //index riceve l'indice dalla sezione personaggi, campindex da quella campagne
        //se entrambi sono nulli si usa l'inizializzazione per membro della campagna
        val index = intent.getStringExtra("pos")
        val campIndex = intent.getStringExtra("camp")
        Log.e("Controllo intent", "$index e poi $campIndex")

        if(index == null) {
            //se non ho index sono tra i personaggi della campagna
            if(campIndex == null) {
                //se non ho indici estraggo il personaggio della campagna corrente (con currentCamp)
                Log.e("check campagna1", CampaignActivity.currentCamp.toString())
                chosenChar = runBlocking { CampaignActivity.estraiPers() }
                Log.e("check campagna2", chosenChar.pgName)
                namePgSel = chosenChar.pgName
            }else {
                //se ho campindex sono il dm di una campagna
                chosenChar = CampaignActivity.sheetList[Integer.parseInt(campIndex)]
            }
        }else {
            //se ho un index significa che sono tra i personaggi locali
            chosenChar = initialize(index)
        }

        val txtName = findViewById<TextView>(R.id.txtCharName)
        txtName.text = chosenChar.pgName

        val statsFrag = StatsFragment()
        fm.beginTransaction().replace(R.id.sheetPortionContainer, statsFrag).commit()

        val saveBtn = findViewById<ImageView>(R.id.icnSave)
        saveBtn.setOnClickListener{
            save()
        }
    }

    //funzioni dell'interfaccia per lo scambio di frammenti
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

    fun getCharacterList(): ArrayList<Characters> {
        var fileName = "characters.json"
        var file = File(filesDir, fileName)
        val jsonString = file.readText()
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)

        return chars
    }

    fun getPg(name : String) : Pg{
        val fileName = "$namePgSel.json"
        val file = File(filesDir, fileName)
        val jsonString = file.readText()
        val gson = Gson()
        val chosenPg = object : TypeToken<Pg>() {}.type
        var chosenPg2 : Pg

        //prova a estrarre un personaggio o lo genera vuoto nel caso in cui sia stato creato
        // solo il file (nuovo personaggio appena creato)
        try {
            chosenPg2 = gson.fromJson(jsonString, chosenPg)
        }catch(e: Exception){
            //se il file è vuoto crea un personaggio nuovo
            chosenPg2 = Pg()
        }

        return chosenPg2
    }

    fun initialize(pos : String?) : Pg {
        var index = pos?.toInt()
        var chars = getCharacterList()

        namePgSel = chars[index!!].name
        val chosenPg = getPg(chars[index!!].name)

        findViewById<TextView>(R.id.txtCharName).text = namePgSel

        return chosenPg
    }

    fun save(){

        if(campaignChar){
            //se edito un personaggio di una campagna lo salvo direttamente in remoto
            //salvo con il campo idowner in modo che sia sempre chiaro di chi sia il personaggio
            runBlocking { remoteSave() }
            Log.e("SheetActivity Save", "camp char")
        }else {
            //copia del contenuto del file json nello storage interno se è un pers normale
            val gson = Gson()
            var jsonString = gson.toJson(chosenChar)

            var fileName: String = "$namePgSel.json"
            var file = File(filesDir, fileName)
            val writer = BufferedWriter(FileWriter(file, false))
            writer.use {
                it.write(jsonString)
                it.newLine()
            }
            Log.e("SheetActivity Save", "success")
            Toast.makeText(this, this.getString(R.string.saveSucc), Toast.LENGTH_SHORT)
        }
    }

    private suspend fun remoteSave(){
        var user = chosenChar.idOwner
        val storageF = Firebase.firestore

        val groupsRef = storageF.collection("groups")
            .document(CampaignActivity.currentCamp.id.toString())
            .collection("chars")
            .document("$user.json")
            .set(chosenChar).await()

        Log.e("SheetActivity RemSave", "DocumentSnapshot successfully written!")
        Toast.makeText(this, this.getString(R.string.saveSuccRem), Toast.LENGTH_SHORT)
    }
}