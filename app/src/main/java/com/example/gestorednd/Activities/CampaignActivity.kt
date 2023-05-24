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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class CampaignActivity : AppCompatActivity() {

    private lateinit var adapter : SheetListAdapterCamp
    private lateinit var recyclerView : RecyclerView
    private var charList : ArrayList<Characters> = arrayListOf()

    companion object {
        lateinit var currentCamp : Campaigns
        var sheetList = ArrayList<Pg>()

        var chosenChar = Pg()

        suspend fun estraiPers(): Pg {
            //recupero il personaggio usato nella campagna
            val user = FirebaseAuth.getInstance().currentUser?.uid
            val storageF = Firebase.firestore

            val champSnapshot = storageF.collection("groups")
                .document(currentCamp.id.toString())
                .collection("chars")
                .document("$user.json").get().await()

            return castToPg(champSnapshot.data)
        }

        private fun castToPg(data: Map<String, Any>?): Pg {
            return Pg(
                data?.get("idOwner") as String?,

                data?.get("pgName") as String? ?: "",
                data?.get("species") as String? ?: "",
                data?.get("clss") as String? ?: "",
                (data?.get("lvl") as Long?)?.toInt() ?: 0,

                (data?.get("hp") as Long?)?.toInt() ?: 0 ,
                (data?.get("ac") as Long?)?.toInt() ?: 0,
                (data?.get("speed") as Long?)?.toInt() ?: 0,
                (data?.get("profBonus") as Long?)?.toInt() ?: 0,
                (data?.get("initBonus") as Long?)?.toInt() ?: 0,

                (data?.get("str") as Long?)?.toInt() ?: 0,
                (data?.get("dex") as Long?)?.toInt() ?: 0,
                (data?.get("con") as Long?)?.toInt() ?: 0,
                (data?.get("int") as Long?)?.toInt() ?: 0,
                (data?.get("wis") as Long?)?.toInt() ?: 0,
                (data?.get("cha") as Long?)?.toInt() ?: 0,

                (data?.get("acr") as Long?)?.toInt() ?: 0,
                (data?.get("ath") as Long?)?.toInt() ?: 0,
                (data?.get("arc") as Long?)?.toInt() ?: 0,
                (data?.get("dec") as Long?)?.toInt() ?: 0,
                (data?.get("ins") as Long?)?.toInt() ?: 0,
                (data?.get("kno") as Long?)?.toInt() ?: 0,
                (data?.get("med") as Long?)?.toInt() ?: 0,
                (data?.get("per") as Long?)?.toInt() ?: 0,
                (data?.get("ste") as Long?)?.toInt() ?: 0,
                (data?.get("sur") as Long?)?.toInt() ?: 0,

                data?.get("equip") as ArrayList<String>? ?: arrayListOf(),
                data?.get("bag") as ArrayList<String>? ?: arrayListOf(),
                data?.get("spellArray") as ArrayList<Pair<String,Int>>? ?: arrayListOf(),
                data?.get("featArray") as ArrayList<Pair<String,String>>? ?: arrayListOf(),
                )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign)

        //ricezione della posizione della campagna da selezionare
        val number = intent.getStringExtra("pos")
        var index = 0
        if (number != null)
            index = Integer.parseInt(number)

        currentCamp = CampaignsFragment.campList[index]
        //reindirizzamento nel caso in cui l'utente al momento non sia il dm (può accedere solo
        // alla sua scheda)
        if (FirebaseAuth.getInstance().currentUser?.uid != currentCamp.idLeader) {
            //todo: (?) intent che rimanda alla sheet view del personaggio della scheda personaggio
            // con il personaggio usato nella campagna
            SheetActivity.campaignChar = true
            val intent = Intent(this, SheetActivity::class.java)
            this.startActivity(intent)
        }
        findViewById<TextView>(R.id.txtCampNameTitle).text = currentCamp.name

        //creazione del link da copiare per joinare il gruppo
        //TODO: controlla funzione per creazione dello short link e metti in fun separata
        val shareLink = "https://gestorednd.page.link/group?id=" + currentCamp.id
        val txtLink = findViewById<TextView>(R.id.txtLink)
        txtLink.text = shareLink

        /*var link : Uri
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse(shareLink)
            domainUriPrefix = "https://gestorednd.page.link"
            setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            buildShortDynamicLink()
        }
        val linkFin = dynamicLink.toString()*/
        //txtLink.text = shareLink

        //copia nella clipboard del link quando viene cliccato
        val copyIcon = findViewById<ImageView>(R.id.imgCopyLinkIcon)
        copyIcon.setOnClickListener{
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", txtLink.text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }

        if(FirebaseAuth.getInstance().currentUser?.uid == currentCamp.idLeader) {
            //lista dei personaggi
            val layoutManager = LinearLayoutManager(this)
            recyclerView = findViewById(R.id.lstMembers)
            recyclerView.layoutManager = layoutManager
            runBlocking { getMembers()}
            adapter = SheetListAdapterCamp(this.charList) //uso dell'adapter ad hoc,
            recyclerView.adapter = adapter
        }
    }

    private suspend fun getMembers() {
        val storageF = Firebase.firestore
        val list = ArrayList<Pg>()

        val sheetsSnapshot = storageF.collection("groups")
            .document(currentCamp.id!!.toString())
            .collection("chars").get().await()
        if(!sheetsSnapshot.isEmpty){
            Log.e(ContentValues.TAG, "DocumentSnapshot successfully written!")

            for (sheet in sheetsSnapshot) {

                val pg = castToPg(sheet.data)
                list.add(pg)
            }

            sheetList = list
            Log.e("debug lista char", sheetList[0].hp.toString())

            charList = pgToChar(list)
            Log.e("debug lista char", charList.toString())

        }
    }

    private fun pgToChar(sheetList: ArrayList<Pg>): ArrayList<Characters> {
        var list : ArrayList<Characters> = arrayListOf()
        for(pg in sheetList)
            list.add(Characters(pg.pgName, pg.species, pg.clss, pg.lvl))

        return list
    }



}