package com.example.gestorednd.MainMenuFragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.MainActivity
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.R
import com.example.gestorednd.Adapters.SheetListAdapter
import com.example.gestorednd.DataClasses.Pg
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

class SheetFragment : Fragment() {

    private lateinit var adapter : SheetListAdapter
    private lateinit var recyclerView : RecyclerView
    companion object {
        lateinit var charList: ArrayList<Characters>

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sheet, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //disabilita le icone di salvataggio cloud se si è in modalità offline cosè che non siano
        // cliccate per sbaglio
        if(MainActivity.offline == true){
            view.findViewById<ImageView>(R.id.icnSyncSheets).isClickable = false
            view.findViewById<ImageView>(R.id.icnSyncSheets).isInvisible = true
            view.findViewById<ImageView>(R.id.icnUploadSheets).isClickable = false
            view.findViewById<ImageView>(R.id.icnUploadSheets).isInvisible = true
        }else{
            view.findViewById<ImageView>(R.id.icnSyncSheets).isClickable = true
            view.findViewById<ImageView>(R.id.icnSyncSheets).isInvisible = false
            view.findViewById<ImageView>(R.id.icnUploadSheets).isClickable = true
            view.findViewById<ImageView>(R.id.icnUploadSheets).isInvisible = false
        }

        //lista dei personaggi
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.sheetView)
        recyclerView.layoutManager = layoutManager
        adapter = SheetListAdapter(initial()) //uso dell'adapter ad hoc
        recyclerView.adapter = adapter

        val btnNewChar = view.findViewById<Button>(R.id.btnNewChar)
        btnNewChar.setOnClickListener { //popup per inserire il nuovo personaggio
            insertChar()
        }

        val btnUpload = view.findViewById<ImageView>(R.id.icnUploadSheets)
        btnUpload.setOnClickListener{
            upload(adapter)
        }

        val btnSync = view.findViewById<ImageView>(R.id.icnSyncSheets)
        btnSync.setOnClickListener{
            sync(adapter)
        }
    }

    private fun initial(): ArrayList<Characters> {
        val filename = "characters.json"
        val file = File(context?.filesDir, filename)
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
            Log.e("SheetFragment", "Initial file Creation Error ")
        }

        charList = chars //per refresh dell'adapter

        //caricamento da asset
        /*val jsonString = context?.assets?.open("characters.json")?.bufferedReader().use {
            it?.readText()
        }
        val gson = Gson()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)*/

        return chars

    }

    fun insertChar(){
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.popup_new_char)
        val nameP = dialog.findViewById<EditText>(R.id.txtCharNamePop)
        val specP = dialog.findViewById<EditText>(R.id.txtCharSpecPop)
        val classP = dialog.findViewById<EditText>(R.id.txtCharClassPop)
        val lvlP = dialog.findViewById<EditText>(R.id.txtCharLvlPop)
        val buttonAdd = dialog.findViewById<Button>(R.id.btnAddPopup)

        //creazione del personaggio alla conferma
        buttonAdd.setOnClickListener {
            //aggiunta del nuovo personaggio alla lista
            try {
                if (
                    !nameP.text.toString().isEmpty()
                    || !specP.text.toString().isEmpty()
                    || !classP.text.toString().isEmpty()
                    || Integer.parseInt(lvlP.text.toString()) != null
                ) {
                    val char = Characters(
                        nameP.text.toString(),
                        specP.text.toString(),
                        classP.text.toString(),
                        Integer.parseInt(lvlP.text.toString())
                    )

                    //creazione file del nuovo personaggio
                    val filename = nameP.text.toString() + ".json"
                    var file = File(context?.filesDir, filename)
                    if (file.exists()) { //creazione file personaggio se il nome non è usato
                        dialog.findViewById<TextView>(R.id.txtPopErr).text = "Error"
                    } else {
                        charList.add(char)
                        adapter = SheetListAdapter(charList)
                        recyclerView.adapter = adapter

                        file.createNewFile()
                        val writer2 = BufferedWriter(FileWriter(file, false))
                        val gson2 = Gson()
                        writer2.use {
                            it.write(
                                gson2.toJson(
                                    Pg(
                                        FirebaseAuth.getInstance().currentUser?.uid,
                                        "",
                                        char.name,
                                        char.specie,
                                        char.clss,
                                        char.lvl
                                    )
                                )
                            )
                            it.newLine()
                        }
                    }

                    //aggiornamento del file con la lista di personaggi
                    file = File(context?.filesDir, "characters.json")
                    file.createNewFile()
                    val writer = BufferedWriter(FileWriter(file, false))
                    val gson = Gson()
                    writer.use {
                        it.write(gson.toJson(charList))
                        it.newLine()
                    }


                } else {
                    dialog.findViewById<TextView>(R.id.txtPopErr).text = "Error"
                }
            }catch (e: Exception){
                dialog.findViewById<TextView>(R.id.txtPopErr).text = "Error"
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    //salva in remoto i files dei personaggi
    fun upload(adapter: SheetListAdapter) {
        //sincronizzo per scaricare eventuali personaggi che non sono ancora stati scaricati
        sync(this.adapter)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //upload file di lista
        val myref = storageRef.child( "$user/characters.json")
        var file = File(context?.filesDir, "characters.json")
        myref.putFile(file.toUri())
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                Log.e("SheetFrag", "Upload Failed 1")
            }

        //acquisisce il vettore dei personaggi per salvarli remotamente tutti
        var jsonString = file.readText()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        val gson = Gson()
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
        for(pers in chars) { //upload di tutte le schede personaggio
            val myref = storageRef.child("$user/${pers.name}.json")
            val file2 = File(context?.filesDir, "${pers.name}.json")
            myref.putFile(file2.toUri())
                .addOnSuccessListener {
                }
                .addOnFailureListener { exception ->
                    Log.e("SheetFrag", "Upload Failed 2")
                }
        }

        //refresh dell'adapter della lista
        adapter.notifyDataSetChanged()
    }

    //download del file con la lista dei personaggi e dei personaggi singoli
    //la funzione gestisce anche i files salvati in locale e non ancora caricati
    fun sync(adapter: SheetListAdapter) {
        fixUnsavedChars()
        var file = File(context?.filesDir, "characters.json")
        if (!file.exists()) return

        //prendo l'array con i personaggi registrati (suppongo già un passaggio di fixUnsavedChars())
        val jsonString = file.readText()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        val gson = Gson()
        var chars: ArrayList<Characters>? = null
        try {
            chars = gson.fromJson(jsonString, listCharactersType)
        }catch(e: java.lang.NullPointerException){
            Log.e("SheetFrag", "Sync Failed 1")
        }

        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference
        val myref = storageRef.child("$user/characters.json")
        val file2 = File(context?.filesDir, "characters.json")
        myref.getFile(file2)
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                Log.e("SheetFrag", "Sync failed 2")
            }

        //faccio la stessa cosa col file proveniente da remoto
        var chars2: ArrayList<Characters>? = null
        try {
            var jsonString2 = file2.readText()

            val listCharactersType2 = object : TypeToken<ArrayList<Characters>>() {}.type
            val gson2 = Gson()
            chars2 = gson2.fromJson(jsonString2, listCharactersType2)
        }catch(e: java.lang.NullPointerException){

        }

        //se un elemento in locale non è nella lista in remoto lo aggiungo e poi concludo scaricando
        // i files personaggio da remoto
        if(chars != null){
            if(!(chars?.isEmpty())!!) {
                for (char in chars!!)
                    if (!chars2!!.contains(char))
                        chars2.add(char)
            }
        }

        if(chars2 != null){
            if(!(chars2?.isEmpty())!!) {
                //falliranno soltanto quelli non salvati in remoto ma in locale
                for (pers in chars2!!) {
                    //upload di tutte le schede personaggio
                    val myref = storageRef.child("$user/${pers.name}.json")
                    val file = File(context?.filesDir, "${pers.name}.json")
                    myref.getFile(file)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { exception ->
                            Log.e("SheetFrag", "Sync Failed 3")
                        }
                }
            }
        }

        //ricostruisce il layout
        adapter.notifyDataSetChanged()
    }

    //pulisce i personaggi rimasti se il file dei personaggi non è presente o li rimuove se non sono
    //registrati (non sarebbero comunque accessibil)
    fun fixUnsavedChars(){
        var file = File(context?.filesDir, "characters.json")
        if (!file.exists()) { //pulisce i files personaggio se non c'è un file characters.json
            file.createNewFile()
            context?.filesDir?.listFiles { name -> name.endsWith(".json") }?.forEach {
                it.delete()
            }

        }else { //rimuove i personaggi i cui files personaggio non sono presenti dalla lista
            try {
                val jsonString = file.readText()

                val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
                val gson = Gson()
                var chars: ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
                for (i in chars.indices.reversed()) { //upload di tutte le schede personaggio
                    var pers = chars[i]
                    val file2 = File(context?.filesDir, "${pers.name}.json")
                    if (!file2.exists()) {
                        chars.removeAt(i)
                    }

                }
            }catch(e:java.lang.NullPointerException){
                file.createNewFile()
                context?.filesDir?.listFiles { name -> name.endsWith(".json") }?.forEach {
                    it.delete()
                }
            }
        }
        //ricostruisce il layout
        view?.invalidate()

        return
    }


}