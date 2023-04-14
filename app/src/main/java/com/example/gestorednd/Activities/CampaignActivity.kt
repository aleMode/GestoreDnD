package com.example.gestorednd.Activities

import android.app.Dialog
import android.content.*
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
import com.example.gestorednd.Adapters.SheetListAdapterCamp
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class CampaignActivity : AppCompatActivity() {

    private lateinit var adapter : SheetListAdapterCamp
    private lateinit var recyclerView : RecyclerView
    private lateinit var charList : ArrayList<Characters>

    companion object {
        lateinit var currentCamp : Campaigns
        lateinit var sheetList : ArrayList<Pg>

        var chosenChar = Pg()

        fun estraiPers(): Pg {
            //recupero il personaggio usato nella campagna
            val user = FirebaseAuth.getInstance().currentUser?.uid
            val storageF = Firebase.firestore

            val champ = storageF.collection("groups").document(currentCamp.id.toString())
                .collection("chars").document("$user.json").get() as Pg

            return champ
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign)

        //ricezione della posizione della campagna da selezionare
        val index = Integer.parseInt(intent.getStringExtra("pos"))
        currentCamp = CampaignsFragment.campList[index]
        //reindirizzamento nel caso in cui l'utente al momento non sia il dm (può accedere solo
        // alla sua scheda)
        if(FirebaseAuth.getInstance().currentUser?.uid != currentCamp.idLeader){
            //intent che rimanda alla sheet view del personaggio della scheda personaggio
            // con il personaggio usato nella campagna
            val intent = Intent(this, SheetActivity::class.java)
            this.startActivity(intent)
        }

        findViewById<TextView>(R.id.txtCampNameTitle).text = currentCamp.name
        val saveBtn = findViewById<ImageView>(R.id.icnSave2)
        saveBtn.setOnClickListener{
            save()
        }

        //creazione del link da copiare per joinare il gruppo
        //TODO: controlla funzione per creazione dello short link e metti in fun separata
        val shareLink = "group?id=" + currentCamp.id
        val txtLink = findViewById<TextView>(R.id.txtLink)
        var link : Uri
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse(shareLink)
            domainUriPrefix = "https://gestorednd.page.link"
            setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            buildShortDynamicLink()
        }
        val linkFin = dynamicLink.toString()

        //intercettazione del link con intent
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                Log.w(ContentValues.TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                // Get the deep link from the Dynamic Link data
                val deepLink = pendingDynamicLinkData?.link
                if (deepLink == null) return@addOnSuccessListener
                Log.w(ContentValues.TAG, "diocaneeeeeeeeeeeeeeeeeeeeeeeeee")

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
        sheetList  = getMembers()
        charList = pgToChar(sheetList)
        adapter = SheetListAdapterCamp(this.charList) //uso dell'adapter ad hoc,
        recyclerView.adapter = adapter
    }

    private fun pgToChar(sheetList: ArrayList<Pg>): ArrayList<Characters> {
        var list : ArrayList<Characters> = arrayListOf()
        for(pg in sheetList)
            list.add(Characters(pg.pgName, pg.species, pg.clss, pg.lvl))

        return list
    }

    private fun getMembers(): ArrayList<Pg> {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageF = Firebase.firestore
        val list = ArrayList<Pg>()

        val groupsRef = storageF.collection("groups").document(currentCamp.id!!.toString())
            .collection("chars").get()
            .addOnSuccessListener { sheets ->
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")

                for (sheet in sheets) {
                    val pg = sheet as Pg
                    list.add(pg)
                }
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        return list
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
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_char_selection)
        dialog.layoutInflater.inflate(R.layout.popup_char_selection, findViewById(android.R.id.content))

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


    private fun setup(currentCamp: Campaigns) {
        //TODO: prende dal firestorage cloud i personaggi e i dati della campagna



    }


    private fun save() {
        //TODO: salva remotamente le schede personaggio editate dal dm
    }
}