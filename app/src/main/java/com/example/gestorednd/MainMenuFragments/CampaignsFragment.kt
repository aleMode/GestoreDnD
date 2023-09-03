package com.example.gestorednd.MainMenuFragments

import android.app.Dialog
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
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
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
        var campList : ArrayList<Campaigns> = arrayListOf()
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

        //lista delle campagne
        Log.e("CampFragment", "lista campagne ${CampaignsFragment.campList}")
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.CampaignsView)
        recyclerView.layoutManager = layoutManager
        adapter = CampaignListAdapter(initial())
        recyclerView.adapter = adapter

        sync(adapter)

        val btnNewCamp = view.findViewById<Button>(R.id.btnNewCamp)
        btnNewCamp.setOnClickListener { //popup per inserire il nuovo personaggio
            insertCamp()
        }

        val btnUpload = view.findViewById<ImageView>(R.id.icnUploadCamp)
        btnUpload.setOnClickListener{
            upload(adapter)
        }

        val btnSync = view.findViewById<ImageView>(R.id.icnSyncCamp)
        btnSync.setOnClickListener{
            sync(adapter)
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

                    //aggiorno la lista col cloud nel caso in cui sia già stata updatata ma non scaricata
                    sync(adapter)

                    campList.add(camp)
                    adapter = CampaignListAdapter(campList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()

                    //aggiornamento del file con la lista delle campagne
                    val file = File(context?.filesDir, "campaigns.json")
                    file.createNewFile()
                    val writer = BufferedWriter(FileWriter(file, false))
                    val gson = Gson()
                    writer.use {
                        it.write(gson.toJson(campList))
                        it.newLine()
                    }

                    /*//TODO: rimuovi questa parte fino ad upload
                    val file2 = File(context?.filesDir, "campaigns.json") // Replace with the path of your file
                    val stringBuilder = StringBuilder()
                    try {
                        val bufferedReader = BufferedReader(FileReader(file2))
                        var line: String?
                        while (bufferedReader.readLine().also { line = it } != null) {
                            stringBuilder.append(line).append("\n")
                        }
                        bufferedReader.close()
                    } catch (e: IOException) {
                        // Handle file reading error
                        Log.e("FileRead", "Error reading file: ${e.message}")
                    }
                    val fileContent = stringBuilder.toString()
                    Log.e("CampFragment", "lista campagne2: $fileContent")

                    Log.e("CampFragment", "lista campagne3: ${CampaignsFragment.campList}")*/

                    upload(adapter)

                    //creazione file su storage remoto della campagna
                    val groupsRef = storageF.collection("groups")
                        .document(camp.id.toString())
                        .collection("data")
                        .document(camp.id.toString()).set(hashMapOf(
                            "camp_id" to camp.id,
                            "leader_id" to camp.idLeader,
                            "name" to camp.name
                        ))
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            Log.e("CampFrag", "Remote creation Failed")
                        }

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
    fun upload(adapter: CampaignListAdapter) {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //upload file di lista
        val myref = storageRef.child( "$user/campaigns.json")
        var file = File(context?.filesDir, "campaigns.json")
        if(file.exists()) {
            myref.delete()
            myref.putFile(file.toUri())
                .addOnSuccessListener {
                    //Toast.makeText(context, resources.getString(R.string.succ),Toast.LENGTH_SHORT)
                }
                .addOnFailureListener { exception ->
                    //Toast.makeText(context, resources.getString(R.string.fail),Toast.LENGTH_SHORT)
                }
        }

        adapter.notifyDataSetChanged()
    }

    //download del file con la lista delle campagne
    fun sync(adapter: CampaignListAdapter) {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //download file di lista
        val myref = storageRef.child( "$user/campaigns.json")
        val file = File(context?.filesDir, "campaigns.json")
        myref.getFile(file.toUri())
            .addOnSuccessListener {
                //Toast.makeText(context, resources.getString(R.string.succ),Toast.LENGTH_SHORT)
            }
            .addOnFailureListener { exception ->
                //Toast.makeText(context, resources.getString(R.string.fail),Toast.LENGTH_SHORT)
            }

        //aggiorno la lista delle campagne dello user
        if(file.exists()) {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<Campaigns>>() {}.type
            val json : String
            if(file.readText() == null || file.readText() == "") {
                campList = arrayListOf()
            }
            else {
                json = file.readText()
                campList = gson.fromJson(json, type)
            }
        }

        //ricostruisce il layout
        adapter.notifyDataSetChanged()
    }


}