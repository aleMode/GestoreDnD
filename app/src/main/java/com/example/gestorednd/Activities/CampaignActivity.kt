package com.example.gestorednd.Activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.SheetListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.MainMenuFragments.CampaignsFragment
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

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

                // TODO: Add the user to the group with the given ID
            }
            .addOnFailureListener(this) { e ->
                // Handle errors
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

    private fun setup(currentCamp: Campaigns) {
        //TODO: prende dal firestorage cloud i personaggi e i dati della campagna



    }


    private fun save() {
        //TODO: salva remotamente le schede personaggio editate dal dm
    }
}