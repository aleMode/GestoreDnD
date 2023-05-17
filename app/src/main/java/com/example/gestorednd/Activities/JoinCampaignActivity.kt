package com.example.gestorednd.Activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.PopupSheetListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.MainMenuFragments.CampaignsFragment
import com.example.gestorednd.MainMenuFragments.SheetFragment
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.util.*

class JoinCampaignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_campaign)

        //snippet for debugging to clear the local file
        var filename = "campaigns.json"
        var file = File(this.filesDir, filename)
        file.delete()

        val link = getCampaignLink()
        if(!link.contains("error"))
            joinCampaign(link)
    }

    //intercettazione del link con intent
    private fun getCampaignLink(): String {

        Log.w(ContentValues.TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

        // Get the deep link from the intent
        var deepLink : String
        if (intent.dataString == null){
            Log.e("Join", "intent non ricevuto")
            return "error"
        }
        deepLink = intent.dataString!!
        Log.w(ContentValues.TAG, "link $deepLink")
        return deepLink
    }

    private fun joinCampaign(deepLink: String) {
        // Parse the deep link to get the group ID
        val parts = deepLink.split("=")
        val groupId = parts[1]
        if(!alreadyJoined(groupId)){
            Log.e("FileUtils", "dio australopiteco ")
            initial()

            val layoutManager = LinearLayoutManager(this)
            var recyclerView = findViewById<RecyclerView>(R.id.lstJoiningChars)
            recyclerView.layoutManager = layoutManager
            var adapter = PopupSheetListAdapter(SheetFragment.charList) //uso dell'adapter ad hoc
            recyclerView.adapter = adapter

            var char : Characters? = null

            val btnQuit = findViewById<Button>(R.id.btnJoinCharCanc)
            btnQuit.setOnClickListener {
                //TODO: finisci qui il return a una attività
            }
            val btnOk = findViewById<Button>(R.id.btnJoinCharOk)
            btnOk.setOnClickListener {
                char = adapter.getSelected()

                if(char == null){
                    Log.e("Debug", "dio culo bastarda ")

                    val intent = Intent(this, MenuActivity::class.java)
                    this.startActivity(intent)
                }

                val camp = remotejoin(char, groupId)
                localjoin(char, groupId, camp!!)
                Log.w(ContentValues.TAG, "diocaneeeeeeeeeeeeeeeeeeeeeeeeee")
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
            }
            Log.e("FileUtils", "dio scimmia bastarda ")

        }else{
            Log.w(ContentValues.TAG, "già membro o dm scemo")
        }


    }

    //carica i personaggi presenti
    private fun initial() {
        //caricamento personaggi locali
        var filename = "characters.json"
        var file = File(this.filesDir, filename)
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

        SheetFragment.charList = chars //per refresh dell'adapter
        Log.e("debug", chars.toString())

        /*//caricamento campagne locali
        filename = "campaigns.json"
        file = File(this.filesDir, filename)
        var camps : ArrayList<Campaigns> = arrayListOf()
        try {
            val jsonString = file.readText()
            val gson = Gson()
            val listCharactersType = object : TypeToken<ArrayList<Campaigns>>() {}.type
            camps = gson.fromJson(jsonString, listCharactersType)

        } catch (e: FileNotFoundException) {
            //se il file non esiste crealo
            file.createNewFile()
            camps = arrayListOf()
        } catch (e: Exception) {
            Log.e("FileUtils", "Error ")
            camps = arrayListOf()
        }

        CampaignsFragment.campList = camps //per refresh dell'adapter */
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

    //query to join the firebase campaign and to save there a copy of the character
    private fun remotejoin(char: Characters?, groupId: String?): Campaigns? {
        //recupero e copio il personaggio richiesto
        val fileName = char?.name!! +".json"
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
        var storageF = Firebase.firestore
        val groupSet = storageF.collection("groups").document(groupId!!)
            .collection("data").document(groupId!!)
            .update("members", FieldValue.arrayUnion(user))

        val groupsRef = storageF.collection("groups").document(groupId!!)
            .collection("data").document("$user.json").set(chosenPg2)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }


        var camp : Campaigns? = runBlocking {
            retrieveCamp(groupId)
        }
        /*val camppp = storageF.collection("groups")
            .document(groupId!!)
            .collection("data")
            .document("82255acc-5b71-4a85-9836-ce0c685fa364")
        camppp.get()
            .addOnSuccessListener { snapshot ->

                val data = snapshot.data
                Log.e("debug2", data.toString())

                camp = Campaigns(
                    data!!.get("name") as String,
                    UUID.fromString(groupId),
                    data!!.get("leader_id") as String
                )
                //idL = snapshot.toObject<Campaigns>()!!

                //nameCamp = snapshot.getString("name") as String
            }
            .addOnFailureListener{ exception ->
                Log.e("debug", "culo" + exception)

            }*/


        Log.e("debug3", camp.toString())

        return camp
    }

    //async function to retrieve correctly the campaign
    private suspend fun retrieveCamp(groupId: String?) : Campaigns {
        val db = Firebase.firestore
        val documentSnapshot = db.collection("groups")
            .document(groupId!!)
            .collection("data")
            .document(groupId!!).get().await()
        var value : Campaigns? = null
        if (documentSnapshot.exists()) {
            Log.e("debug", "halp")

            value = Campaigns(
                documentSnapshot.data!!["name"] as String,
                UUID.fromString(groupId),
                documentSnapshot.data!!["leader_id"] as String
            )
            Log.e("debug", value.toString())

        }
        return value!!
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

        CampaignsFragment.campList.add(camp)

        //aggiornamento del file con la lista delle campagne
        file = File(this.filesDir, "campaigns.json")
        file.createNewFile()
        val writer = BufferedWriter(FileWriter(file, false))
        val gson = Gson()
        Log.e("debug", CampaignsFragment.campList.toString())

        writer.use {
            it.write(gson.toJson(CampaignsFragment.campList))
            it.newLine()
            it.flush()
        }
        writer.close()

        //upload del file delle campagne
        myref.putFile(file.toUri())
            .addOnSuccessListener {
                Log.e("debug", "upload ok")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }
    }


}