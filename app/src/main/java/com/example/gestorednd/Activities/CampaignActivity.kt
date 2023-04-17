package com.example.gestorednd.Activities

import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.PopupSheetListAdapter
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
        var index = Integer.parseInt(intent.getStringExtra("pos"))

        currentCamp = CampaignsFragment.campList[index]
        //reindirizzamento nel caso in cui l'utente al momento non sia il dm (può accedere solo
        // alla sua scheda)
        if (FirebaseAuth.getInstance().currentUser?.uid != currentCamp.idLeader) {
            //intent che rimanda alla sheet view del personaggio della scheda personaggio
            // con il personaggio usato nella campagna
            val intent = Intent(this, SheetActivity::class.java)
            this.startActivity(intent)
        }
        findViewById<TextView>(R.id.txtCampNameTitle).text = currentCamp.name

        //creazione del link da copiare per joinare il gruppo
        //TODO: controlla funzione per creazione dello short link e metti in fun separata
        val shareLink = "https://gestorednd.page.link/group?id=" + currentCamp.id
        val txtLink = findViewById<TextView>(R.id.txtLink)
        var link : Uri
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse(shareLink)
            domainUriPrefix = "https://gestorednd.page.link"
            setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            buildShortDynamicLink()
        }
        val linkFin = dynamicLink.toString()
        Log.w(ContentValues.TAG, "diocaneeeeeeeeeeeeeeeeeeeeeeeeee ma prima")

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

}