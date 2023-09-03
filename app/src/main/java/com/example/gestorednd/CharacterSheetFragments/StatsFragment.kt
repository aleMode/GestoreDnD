package com.example.gestorednd.CharacterSheetFragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getStorageDirectory
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.gestorednd.Activities.CampaignActivity
import com.example.gestorednd.Activities.SheetActivity
import com.example.gestorednd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.security.cert.Extension
import java.util.*


class StatsFragment() : Fragment() {

    private val CAMERA_CAPTURE_REQUEST = 1
    private val CAMERA_PERMISSION_CODE = 101
    private val SELECT_IMAGE_REQUEST = 2
    private val STORAGE_PERMISSION_CODE = 102

    private var currentPhotoPath : Uri = "".toUri()
    private lateinit var currentPhotoName : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hpVal = view.findViewById<TextView>(R.id.txtHpV)
        hpVal.text = SheetActivity.chosenChar.hp.toString()

        hpVal.setOnFocusChangeListener { view, b -> //aggiorna i valori dopo il cambiamento
            if (!b) {
                SheetActivity.chosenChar.hp = Integer.parseInt(hpVal.text.toString())
            }
        }

        val hdVal =
            view.findViewById<TextView>(R.id.txtHdV) //la vita segue i livelli, non editabile
        hdVal.text = SheetActivity.chosenChar.lvl.toString()

        val caVal = view.findViewById<TextView>(R.id.txtCaV)
        caVal.text = SheetActivity.chosenChar.ac.toString()
        caVal.setOnFocusChangeListener { view, b ->
            if (!b) {
                SheetActivity.chosenChar.ac = Integer.parseInt(caVal.text.toString())
            }
        }

        val speedVal = view.findViewById<TextView>(R.id.txtSpeedV)
        speedVal.text = SheetActivity.chosenChar.speed.toString()
        speedVal.setOnFocusChangeListener { view, b ->
            if (!b) {
                SheetActivity.chosenChar.speed = Integer.parseInt(speedVal.text.toString())
            }
        }

        val profVal = view.findViewById<TextView>(R.id.txtProfV)
        profVal.text = SheetActivity.chosenChar.profBonus.toString()
        profVal.setOnFocusChangeListener { view, b ->
            if (!b) {
                SheetActivity.chosenChar.profBonus = Integer.parseInt(profVal.text.toString())
            }
        }

        val initVal = view.findViewById<TextView>(R.id.txtInitV)
        initVal.text = SheetActivity.chosenChar.initBonus.toString()
        initVal.setOnFocusChangeListener { view, b ->
            if (!b) {
                SheetActivity.chosenChar.initBonus = Integer.parseInt(initVal.text.toString())
            }
        }

        //TODO: prendi l'immagine scaricata col personaggio e aprila
        val img_slot = view.findViewById<ImageView>(R.id.img_char)
        if (SheetActivity.chosenChar.imgPath != "") {
            if(SheetActivity.campaignChar){
                val storageRef = Firebase.storage.reference
                val myref = storageRef.child(
                    "${SheetActivity.chosenChar.idOwner}/${SheetActivity.chosenChar.pgName}.jpg"
                )
                val file = File(context?.filesDir, SheetActivity.chosenChar.imgPath)
                myref.getFile(file)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SheetFrag", "Sync failed 2")
                    }
            }else{
                val user = FirebaseAuth.getInstance().currentUser?.uid
                val storageRef = Firebase.storage.reference
                val myref = storageRef.child(
                    "${user}/${SheetActivity.chosenChar.pgName}.jpg"
                )
                val file = File(context?.filesDir, SheetActivity.chosenChar.imgPath)
                myref.getFile(file)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SheetFrag", "Sync failed *")
                    }
            }
            val img = File(
                context!!.filesDir,
                SheetActivity.chosenChar.imgPath
            )
            val bitmap = BitmapFactory.decodeFile(img.absolutePath)
            img_slot.setImageBitmap(bitmap)
        }

        val btn_camera = view.findViewById<Button>(R.id.btn_camera)
        btn_camera.setOnClickListener {
            askCameraPermission()

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_CAPTURE_REQUEST)
        }
        // When the user clicks the button to select an image
        val btn_files = view.findViewById<Button>(R.id.btn_files)
        // Check and request storage permissions
        btn_files.setOnClickListener {
            askStoragePermission()

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }
    }

    private fun askCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun askStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var selectedBitmap: Bitmap? = null

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                // Handle image selected from gallery
                val selectedImageUri: Uri? = data.data
                selectedBitmap =
                    MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImageUri)
            } else if (requestCode == CAMERA_CAPTURE_REQUEST && data != null) {
                // Handle image captured from camera
                selectedBitmap = data.extras?.get("data") as Bitmap
            }
            val resized = Bitmap.createScaledBitmap(selectedBitmap!!, 400, 400, true)
            view?.findViewById<ImageView>(R.id.img_char)?.setImageBitmap(resized)

            createFileFromImg(resized)

            //TODO: carica online l'immagine se il personaggio è online, salva nel valore immagine il
            //TODO percorso scelto se è una immagine presente in locale, idem ma salvandola anche come
            //TODO file se è una immagine appena scattata.

            //TODO: all'apertura se il pg è in locale l'immagine viene cercata, altrimenti se non si è
            //TODO in locale viene ignorato e l'immagine è scaricata dalla cartella in remoto e applicata
            //TODO a quel punto se l'immagine è cambiata essa è caricata subito e così via
        }
    }

    private fun createFileFromImg(resized: Bitmap) {
        val user = SheetActivity.chosenChar.idOwner
        val imageFileName = user + "_" + SheetActivity.chosenChar.pgName + ".jpg"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        var imageFile = File(context!!.filesDir, imageFileName)
        if(imageFile.exists()) imageFile.delete()
        imageFile.createNewFile()

        var savedImgPath : Uri? = null
        try{
            val outputStream = FileOutputStream(imageFile)
            resized.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            savedImgPath = imageFile.absolutePath.toUri()
            currentPhotoPath = imageFile.absolutePath.toUri()

            Log.e("SaveImg file", "img writing to file successful @: $currentPhotoPath")
        }catch (e: Exception){
            Log.e("SaveImg file", e.printStackTrace().toString())
        }
        currentPhotoName = imageFileName
        saveImg()
    }

    //salva le immagini in locale se il personaggio non è in una campagna, in remoto se si
    private fun saveImg() {
        //salva il percorso nella scheda personaggio
        SheetActivity.chosenChar.imgPath = currentPhotoName

        val gson = Gson()
        var jsonString = gson.toJson(SheetActivity.chosenChar)

        var fileName = "${SheetActivity.namePgSel}.json"
        var file = File(context!!.filesDir, fileName)
        val writer = BufferedWriter(FileWriter(file, false))
        writer.use {
            it.write(jsonString)
            it.newLine()
        }
        Log.e("SheetActivity Save", "save for img success")

        if (SheetActivity.campaignChar) {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child(
                "${SheetActivity.chosenChar.imgPath}/${SheetActivity.chosenChar.pgName}.jpg"
                )
            val imageFile = File(context!!.filesDir, SheetActivity.chosenChar.imgPath)
            Log.e("imgUpload", SheetActivity.chosenChar.imgPath)

            imageRef.putFile(imageFile.toUri())
                .addOnSuccessListener { taskSnapshot ->
                    Log.e("imgUpload", "okok1")

                }
                .addOnFailureListener{
                    Log.e("imgUpload", "failed")
                }

            Toast.makeText(
                context,
                context!!.getString(R.string.saveSuccRem),
                Toast.LENGTH_SHORT
            )
        }else{
            var user = FirebaseAuth.getInstance().currentUser?.uid
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child(
                "${user}/${SheetActivity.chosenChar.pgName}.jpg"
            )
            val imageFile = File(context!!.filesDir, SheetActivity.chosenChar.imgPath)
            Log.e("imgUpload", SheetActivity.chosenChar.imgPath)

            imageRef.putFile(imageFile.toUri())
                .addOnSuccessListener { taskSnapshot ->
                    Log.e("imgUpload", "okok1")

                }
                .addOnFailureListener{
                    Log.e("imgUpload", "failed")
                }

        }


        Toast.makeText(context!!, this.getString(R.string.saveSucc), Toast.LENGTH_SHORT)
    }

}
