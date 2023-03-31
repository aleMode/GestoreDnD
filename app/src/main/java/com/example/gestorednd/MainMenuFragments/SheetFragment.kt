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
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Activities.MainActivity
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.R
import com.example.gestorednd.Adapters.SheetListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

class SheetFragment : Fragment() {

    private lateinit var adapter : SheetListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var charList : ArrayList<Characters>

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

        //disavilità le icone di salvataggio cloud se si è in modalità ffline cosè che non siano
        // cliccate per sbaglio
        if(MainActivity.offline == true){
            view.findViewById<ImageView>(R.id.icnSync).isClickable = false
            view.findViewById<ImageView>(R.id.icnSync).isInvisible = true
            view.findViewById<ImageView>(R.id.icnUpload).isClickable = false
            view.findViewById<ImageView>(R.id.icnUpload).isInvisible = true
        }else{
            view.findViewById<ImageView>(R.id.icnSync).isClickable = true
            view.findViewById<ImageView>(R.id.icnSync).isInvisible = false
            view.findViewById<ImageView>(R.id.icnUpload).isClickable = true
            view.findViewById<ImageView>(R.id.icnUpload).isInvisible = false
        }

        //lista dei personaggi
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.sheetView)
        recyclerView.layoutManager = layoutManager
        adapter = SheetListAdapter(initial()) //uso dell'adapter ad hoc
        recyclerView.adapter = adapter

        val btnNewChar = view.findViewById<Button>(R.id.btnNewChar)
        btnNewChar.setOnClickListener { //popup per inserire roba il nuovo personaggio

        }

        val btnUpload = view.findViewById<ImageView>(R.id.icnUpload)
        btnUpload.setOnClickListener{
            upload()
        }

        val btnSync = view.findViewById<ImageView>(R.id.icnSync)
        btnSync.setOnClickListener{
            sync()
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
            Log.e("FileUtils", "Error ")
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
        dialog.setContentView(R.layout.popup)
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
    fun upload(){
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //upload file di lista
        val myref = storageRef.child( "$user/characters.json")
        var file = File(context?.filesDir, "characters.json")
        val inputStream = FileInputStream(file)
        myref.putStream(inputStream)
            .addOnSuccessListener {
                Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }

        //acquisisce il vettore dei personaggi per salvarli remotamente tutti
        var jsonString = file.readText()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        val gson = Gson()
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
        for(pers in chars) { //upload di tutte le schede personaggio
            val myref = storageRef.child("$user/${pers.name}.json")
            val file = File(context?.filesDir, "${pers.name}.json")
            val inputStream = FileInputStream(file)
            myref.putStream(inputStream)
                .addOnSuccessListener {
                    Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun sync(){
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = Firebase.storage.reference

        //download file di lista
        val myref = storageRef.child( "$user/characters.json")
        val file = File(context?.filesDir, "characters.json")
        myref.getFile(file)
            .addOnSuccessListener {
                Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
            }

        //crea un array con tutti i personaggi per scariarli
        val jsonString = file.readText()
        val listCharactersType = object : TypeToken<ArrayList<Characters>>() {}.type
        val gson = Gson()
        var chars : ArrayList<Characters> = gson.fromJson(jsonString, listCharactersType)
        for(pers in chars) { //upload di tutte le schede personaggio
            val myref = storageRef.child("$user/${pers.name}.json")
            val file = File(context?.filesDir, "${pers.name}.json")
            myref.getFile(file)
                .addOnSuccessListener {
                    Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Operation unsuccessful!", Toast.LENGTH_SHORT).show()
                }
        }

        //ricostruisce il layout
        view?.invalidate()
    }

}