package com.example.gestorednd.Activities

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.PopupSheetListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.MainMenuFragments.CampaignsFragment
import com.example.gestorednd.MainMenuFragments.SheetFragment
import com.example.gestorednd.R
import com.example.gestorednd.R.layout.popup_char_selection
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)

        //tasto per il logout
        val logout = findViewById<ImageView>(R.id.icnLogout)
        logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //FragmentManager e chips per la gestione dei frammenti di schede personaggio
        // e campagne
        val fm : FragmentManager = supportFragmentManager
        val sheetFragment = SheetFragment()
        val campFragment = CampaignsFragment()
        val txtTitle = findViewById<TextView>(R.id.mainTitle)
        val chipGrp = findViewById<ChipGroup>(R.id.selectChips)
        val chipChars = findViewById<Chip>(R.id.sheetChip)
        val chipCamps = findViewById<Chip>(R.id.campaignChip)

        fm.beginTransaction().add(R.id.fragmentContainerView2, sheetFragment).commit()
        txtTitle.text = "Characters"
        chipChars.isChecked = true


        chipCamps.setOnClickListener(){
            fm.beginTransaction().replace(R.id.fragmentContainerView2, campFragment).commit()
            txtTitle.text = "Campaigns"
        }
        chipChars.setOnClickListener(){
            fm.beginTransaction().replace(R.id.fragmentContainerView2, sheetFragment).commit()
            txtTitle.text = "Characters"
        }

        //intercettazione del link con intent
        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                Log.w(ContentValues.TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

                // Get the deep link from the Dynamic Link data
                var deepLink : Uri? = null
                if (pendingDynamicLinkData?.link == null){
                    return@addOnSuccessListener
                }
                deepLink = pendingDynamicLinkData.link
                Log.w(ContentValues.TAG, "link $deepLink")

                // Parse the deep link to get the group ID
                val groupId = deepLink?.getQueryParameter("id")

                if(!alreadyJoined(groupId)){
                    val char : Characters?
                    char = charSelection()
                    if(char == null){
                        val intent = Intent(this, CampaignActivity::class.java)
                        this.startActivity(intent)
                    }

                    val camp = remotejoin(char, groupId)
                    localjoin(char, groupId, camp)
                }else{
                    Log.w(ContentValues.TAG, "già membro o dm scemo")
                }

                Log.w(ContentValues.TAG, "diocaneeeeeeeeeeeeeeeeeeeeeeeeee")
            }
            .addOnFailureListener(this) { e ->
                Log.w(ContentValues.TAG, "error handling link")
            }
    }

    //funzione per capire se il gruppo è già stato joinato
    private fun alreadyJoined(groupId: String?): Boolean {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //download file di lista per averla aggiornata
        val myref = storageRef.child( "$user/campaigns.json")
        var file = File(this?.filesDir, "campaigns.json")
        myref.getFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }

        //prendo il riferimento con le campagne per controllare se quella in questione
        //è già stata joinata
        val filename = "campaigns.json"
        file = File(this.filesDir, filename)
        var camps : ArrayList<Campaigns> = arrayListOf()
        try {
            val jsonString = file.readText()
            val gson = Gson()
            val listCampaignsType = object : TypeToken<ArrayList<Campaigns>>() {}.type
            camps = gson.fromJson(jsonString, listCampaignsType)

        } catch (e: Exception) {
            Log.e("FileUtils", "Error ")
        }

        var joined = false
        for(camp in camps)
            if(camp.id.toString() == groupId)
                joined = true

        return joined
    }

    private fun charSelection(): Characters? {
        //popup to select a character
        //val dialog = Dialog(this)
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(popup_char_selection, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        Log.e("FileUtils", "dio boiaaaaaaaaaaa ")
        //dialog.setContentView(R.layout.popup_char_selection)

        val layoutManager = LinearLayoutManager(this)
        var recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.lstPopupChars)
        recyclerView.layoutManager = layoutManager
        var adapter = PopupSheetListAdapter(SheetFragment.charList) //uso dell'adapter ad hoc
        recyclerView.adapter = adapter

        var pers : Characters? = null

        val btnQuit = findViewById<Button>(R.id.btnPopCharCanc)
        btnQuit.setOnClickListener {
            dialog.dismiss()
        }
        val btnOk = findViewById<Button>(R.id.btnPopCharOk)
        btnOk.setOnClickListener {
            //pers = adapter.getSelected()
            dialog.dismiss()
        }

        return pers
    }

    //query to join the firebase campaign and to save there a copy of the character
    private fun remotejoin(char: Characters?, groupId: String?): Campaigns {
        //recupero e copio il personaggio richiesto
        val fileName = "$char.json"
        val file = File(filesDir, fileName)
        val jsonString = file.readText()
        val gson = Gson()
        val chosenPg = object : TypeToken<Pg>() {}.type
        var chosenPg2 : Pg
        chosenPg2 = gson.fromJson(jsonString, chosenPg)
        chosenPg2.idOwner = FirebaseAuth.getInstance().currentUser?.uid
        chosenPg2.pgName = char!!.name
        chosenPg2.species = char!!.specie
        chosenPg2.clss = char!!.clss

        //caricamento in remote del personaggio e join della campagna
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageF = Firebase.firestore
        val groupSet = storageF.collection("groups").document(groupId!!)
            .update("members", user)
        val groupsRef = storageF.collection("groups").document(groupId!!)
            .collection("chars").document("$user.json").set(chosenPg2)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        var idL : String? = null
        var nameCamp : String? = null
        val idLeader = storageF.collection("groups").document(groupId!!).get()
            .addOnSuccessListener { snapshot ->
                idL = snapshot.get("leader_id") as String
                nameCamp = snapshot.get("name") as String
            }
        val camp = Campaigns(nameCamp!!, UUID.fromString(groupId), idL!!)

        return camp
    }

    //join nelle campagne salvate localmente e synch
    private fun localjoin(char: Characters?, groupId: String?, camp: Campaigns) {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //download file di lista per aggiornare
        val myref = storageRef.child( "$user/campaigns.json")
        var file = File(this.filesDir, "campaigns.json")
        myref.getFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }

        //aggiornamento del file con la lista delle campagne
        file = File(this.filesDir, "campaigns.json")
        file.createNewFile()
        val writer = BufferedWriter(FileWriter(file, false))
        val gson = Gson()
        writer.use {
            it.write(gson.toJson(CampaignsFragment.campList))
            it.newLine()
        }

        CampaignsFragment.campList.add(camp)

        //upload del file delle campagne
        myref.putFile(file.toUri())
            .addOnSuccessListener {
                Toast.makeText(this, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }
    }

}