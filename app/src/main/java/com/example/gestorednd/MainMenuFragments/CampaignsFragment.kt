package com.example.gestorednd.MainMenuFragments

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.CampaignListAdapter
import com.example.gestorednd.Adapters.SheetListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class CampaignsFragment : Fragment() {

    private lateinit var adapter : CampaignListAdapter
    private lateinit var recyclerView : RecyclerView
    companion object{
        lateinit var campList : ArrayList<Campaigns>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_campaigns, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sync()

        //lista delle campagne
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.CampaignsView)
        recyclerView.layoutManager = layoutManager
        adapter = CampaignListAdapter(initial())
        recyclerView.adapter = adapter

        val btnNewCamp = view.findViewById<Button>(R.id.btnNewCamp)
        btnNewCamp.setOnClickListener { //popup per inserire il nuovo personaggio
            insertCamp()
        }

        val btnUpload = view.findViewById<ImageView>(R.id.icnUploadSheets)
        btnUpload.setOnClickListener{
            upload()
        }

        val btnSync = view.findViewById<ImageView>(R.id.icnSyncSheets)
        btnSync.setOnClickListener{
            sync()
        }
    }

    private fun initial(): ArrayList<Campaigns> {
        /* caricamento da asset o generazione
        charList = arrayListOf<Characters>()

        for (i in 1..100) {
            val pers = Characters("antonio $i", "umano", "fuffa", i)
            charList.add(pers)
        }



        var directory : File = File(".${File.pathSeparator}characters")
        directory.mkdirs()
        val file = File(directory, "characters.txt")

        val jsonString = context?.assets?.open("campaigns.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCampaignsType = object : TypeToken<ArrayList<Campaigns>>() {}.type
        var camps : ArrayList<Campaigns> = gson.fromJson(jsonString, listCampaignsType)*/

        val filename = "campaigns.json"
        val file = File(context?.filesDir, filename)
        var camps : ArrayList<Campaigns> = arrayListOf()
        try {
            val jsonString = file.readText()
            val gson = Gson()
            val listCampaignsType = object : TypeToken<ArrayList<Campaigns>>() {}.type
            camps = gson.fromJson(jsonString, listCampaignsType)

        } catch (e: FileNotFoundException) {
            //se il file non esiste crealo
            file.createNewFile()
        } catch (e: Exception) {
            Log.e("FileUtils", "Error ")
        }
        return camps
    }

    fun insertCamp(){
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.popup_new_campaign)
        val nameC = dialog.findViewById<EditText>(R.id.txtGroupName)
        val buttonAdd = dialog.findViewById<Button>(R.id.btnGroupConf)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageF = Firebase.firestore

        //creazione della campagna alla conferma
        buttonAdd.setOnClickListener {
            //aggiunta della nuova campagna alla lista
            try {
                if (!nameC.text.toString().isEmpty()) {
                    val camp = Campaigns(
                        nameC.text.toString(),
                        UUID.randomUUID(),
                        user!!
                    )

                    campList.add(camp)
                    adapter = CampaignListAdapter(campList)
                    recyclerView.adapter = adapter

                    //aggiornamento del file con la lista delle campagne
                    val file = File(context?.filesDir, "campaigns.json")
                    file.createNewFile()
                    val writer = BufferedWriter(FileWriter(file, false))
                    val gson = Gson()
                    writer.use {
                        it.write(gson.toJson(campList))
                        it.newLine()
                    }

                    //creazione file su storage remoto della campagna
                    val groupsRef = storageF.collection("groups")
                        .document(camp.id.toString()).set(hashMapOf(
                            "camp_id" to camp.id,
                            "leader_id" to camp.idLeader,
                            "name" to camp.name
                        ))
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                } else {
                    dialog.findViewById<TextView>(R.id.txtPopCampErr).text = "Error"
                }
            }catch (e: Exception){
                dialog.findViewById<TextView>(R.id.txtPopCampErr).text = "Error"
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    //salva in remoto il file delle campagne
    fun upload(){
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //upload file di lista
        val myref = storageRef.child( "$user/campaigns.json")
        var file = File(context?.filesDir, "campaigns.json")
        val inputStream = FileInputStream(file)
        myref.putStream(inputStream)
            .addOnSuccessListener {
                Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }
    }

    //download del file con la lista delle campagne
    fun sync(){
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //download file di lista
        val myref = storageRef.child( "$user/campaigns.json")
        val file = File(context?.filesDir, "campaigns.json")
        myref.putFile(file.toUri())
            .addOnSuccessListener {
                Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }

        //ricostruisce il layout
        view?.invalidate()
    }


}