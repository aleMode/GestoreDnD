package com.example.gestorednd.Activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestorednd.Adapters.PopupSheetListAdapter
import com.example.gestorednd.DataClasses.Campaigns
import com.example.gestorednd.DataClasses.Characters
import com.example.gestorednd.DataClasses.Pg
import com.example.gestorednd.MainMenuFragments.CampaignsFragment
import com.example.gestorednd.MainMenuFragments.SheetFragment
import com.example.gestorednd.R
import com.example.gestorednd.R.layout.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
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

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)

        //tasto per il logout
        val logout = findViewById<ImageView>(R.id.icnLogout)
        logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //FragmentManager e chips per la gestione dei frammenti di schede personaggio
        // e campagne
        val fm : FragmentManager = supportFragmentManager
        val sheetFragment = SheetFragment()
        val campFragment = CampaignsFragment()
        val txtTitle = findViewById<TextView>(R.id.mainTitle)
        val chipGrp = findViewById<ChipGroup>(R.id.selectChips)
        val chipChars = findViewById<Chip>(R.id.sheetChip)
        val chipCamps = findViewById<Chip>(R.id.campaignChip)

        fm.beginTransaction().add(R.id.fragmentContainerView2, sheetFragment).commit()
        txtTitle.text = "Characters"
        chipChars.isChecked = true


        chipCamps.setOnClickListener(){
            fm.beginTransaction().replace(R.id.fragmentContainerView2, campFragment).commit()
            txtTitle.text = "Campaigns"
        }
        chipChars.setOnClickListener(){
            fm.beginTransaction().replace(R.id.fragmentContainerView2, sheetFragment).commit()
            txtTitle.text = "Characters"
        }

    }



}