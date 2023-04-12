package com.example.gestorednd.Activities

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.PopupSheetListAdapter
import com.example.gestorednd.Adapters.SheetListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.MainMenuFragments.CampaignsFragment
import com.example.gestorednd.MainMenuFragments.SheetFragment
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

class CampaignActivity : AppCompatActivity() {

    private lateinit var adapter : SheetListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var charList : ArrayList<Characters>

    companion object {
        var chosenChar = Pg()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign)

        //ricezione della posizione del personaggio da selezionare
        val index = Integer.parseInt(intent.getStringExtra("pos"))
        val currentCamp = CampaignsFragment.campList[index]
        if(FirebaseAuth.getInstance().currentUser?.uid != currentCamp.idLeader){
            //TODO metti intent che rimandi solo ad una versione modificata (o alla stessa che modifichi) della sheet view del personaggio
        }
        setup(currentCamp)

        val saveBtn = findViewById<ImageView>(R.id.icnSave2)
        saveBtn.setOnClickListener{
            save()
        }

        //creazione del link da copiare per joinare il gruppo
        //TODO: controlla funzione per creazione dello short link
        val shareLink = "app/dndApp/group?id=" + currentCamp.id
        val txtLink = findViewById<TextView>(R.id.txtLink)
        var link : Uri
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse(shareLink)
            domainUriPrefix = "app/gestorednd"
            setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            buildShortDynamicLink()
        }
        val linkFin = dynamicLink.toString()

        //intercettazione del link con intent
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get the deep link from the Dynamic Link data
                val deepLink = pendingDynamicLinkData?.link

                // Parse the deep link to get the group ID
                val groupId = deepLink?.getQueryParameter("id")

                if(!alreadyJoined(groupId)){
                    val char : Characters?
                    char = charSelection()
                    if(char == null){}
                        //TODO: esci dalla situa

                    val camp = remotejoin(char, groupId)
                    localjoin(char, groupId, camp)
                }

            }
            .addOnFailureListener(this) { e ->
                // todo: Handle errors
            }

        txtLink.text = linkFin
        //copia nella clipboard del link quando viene cliccato
        txtLink.setOnClickListener{
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", txtLink.text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }


        //lista dei personaggi
        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.lstMembers)
        recyclerView.layoutManager = layoutManager
        adapter = SheetListAdapter(charList) //uso dell'adapter ad hoc,
        // TODO: fai sta funzione initial ?? e chiamala diversa
        recyclerView.adapter = adapter
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

        var joined : Boolean = false
        for(camp in camps)
            if(camp.id.toString() == groupId)
                joined = true

        return joined
    }

    private fun charSelection(): Characters? {
        //popup to select a character
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_char_selection)

        val layoutManager = LinearLayoutManager(this)
        var recyclerView2 = findViewById<RecyclerView>(R.id.lstPopupChars)
        recyclerView2.layoutManager = layoutManager
        var adapter2 = PopupSheetListAdapter(SheetFragment.charList) //uso dell'adapter ad hoc
        recyclerView2.adapter = adapter2

        var pers : Characters? = null

        val btnQuit = findViewById<Button>(R.id.btnPopCharCanc)
        btnQuit.setOnClickListener {
            dialog.dismiss()
        }
        val btnOk = findViewById<Button>(R.id.btnPopCharOk)
        btnOk.setOnClickListener {
            pers = adapter2.getSelected()
            dialog.dismiss()
        }

        dialog.show()
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

        //caricamento in remote del personaggio e join della campagna
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageF = Firebase.firestore
        val charUUID = UUID.randomUUID()
        val groupSet = storageF.collection("groups").document(groupId!!)
            .update("members", FieldValue.arrayUnion(user, char, charUUID))
        val groupsRef = storageF.collection("groups").document(groupId!!)
            .collection("chars").document("$char.json").set(chosenPg2)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        var idL : String? = null
        var nameC : String? = null
        val idLeader = storageF.collection("groups").document(groupId!!).get()
            .addOnSuccessListener { snapshot ->
                idL = snapshot.get("leader_id") as String
                nameC = snapshot.get("name") as String
            }
        val camp = Campaigns(nameC!!, UUID.fromString(groupId), idL!!)
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


    private fun setup(currentCamp: Campaigns) {
        //TODO: prende dal firestorage cloud i personaggi e i dati della campagna



    }


    private fun save() {
        //TODO: salva remotamente le schede personaggio editate dal dm
    }
}